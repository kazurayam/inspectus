package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.Inspectus;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.StdStepListener;
import com.kazurayam.materialstore.base.manage.StoreCleaner;
import com.kazurayam.materialstore.base.manage.StoreExport;
import com.kazurayam.materialstore.base.manage.StoreImport;
import com.kazurayam.materialstore.base.report.IndexCreator;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.Store;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractService implements Inspectus {

    private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    protected StepListener listener;

    public AbstractService() {
        listener = new StdStepListener();
    }

    @Override
    public void setListener(StepListener listener) {
        Objects.requireNonNull(listener);
        this.listener = listener;
    }

    /*
     * import the lastest artifacts from the remote/backup store
     * into the local/current store
     * @param parameters must contain the keys of "store", "back" and "jobName"
     * @throws InspectusException
     */
    @Override
    public final Intermediates preProcess(Parameters parameters) throws InspectusException {
        return step0_restorePrevious(parameters);
    }

    @Override
    public Intermediates postProcess(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        Intermediates result4 = step4_report(parameters, intermediates);
        Intermediates result5 = step5_backupLatest(parameters, result4);
        Intermediates result6 = step6_cleanup(parameters, result5);
        Intermediates result7 = step7_index(parameters, result6);
        return result7;
    }

    protected final Intermediates step0_restorePrevious(Parameters parameters)
            throws InspectusException {
        listener.stepStarted("step0_restorePrevious");
        Store backup = parameters.getBackup();
        Store store = parameters.getStore();
        JobName jobName = parameters.getJobName();
        JobTimestamp newerThanOrEqualTo = parameters.getJobTimestamp();
        try {
            if (backup != Store.NULL_OBJECT) {
                if (backup.contains(jobName)) {
                    StoreImport importer = StoreImport.newInstance(backup, store);
                    importer.importReports(jobName, newerThanOrEqualTo);
                } else {
                    logger.warn(String.format("JobName %s is not found in the backup store %s",
                            jobName, backup.toString()));
                }
            } else {
                logger.warn("backup is not specified in the parameters." +
                        " will skip restoring the previous JobTimestamp directories from the backup store.");
            }
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
        listener.stepFinished("step0_restorePrevious");
        return Intermediates.builder().build();
    }


    abstract protected Intermediates step4_report(Parameters parameters, Intermediates intermediates)
            throws InspectusException;

    protected Intermediates step5_backupLatest(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step5_backupLatest");
        Store store = parameters.getStore();
        JobName jobName = parameters.getJobName();
        JobTimestamp newerThanOrEqualTo = parameters.getJobTimestamp();
        Store backup = parameters.getBackup();
        try {
            if (backup != Store.NULL_OBJECT) {
                StoreExport export = StoreExport.newInstance(store, backup);
                export.exportReports(jobName, newerThanOrEqualTo);

                // create the index.html of the backup
                IndexCreator indexCreator = new IndexCreator(backup);
                Path index = indexCreator.create();
                listener.info("backup finished : " + index.toString());
            } else {
                logger.warn("backup is not specified." +
                        " will skip exporting the JobTimestamp directories into backup");
            }
        } catch (MaterialstoreException | IOException e) {
            throw new InspectusException(e);
        }
        listener.stepFinished("step5_backupLatest");
        return Intermediates.builder(intermediates).build();
    }

    protected Intermediates step6_cleanup(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step6_cleanup");
        Store store = parameters.getStore();
        JobName jobName = parameters.getJobName();
        try {
            StoreCleaner cleaner = StoreCleaner.newInstance(store);
            cleaner.cleanup(jobName,
                    parameters.getCleanOlderThan());  // default: JobTimestamp.now().minusHours(3)
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
        listener.stepFinished("step6_cleanup");
        return Intermediates.builder(intermediates).build();
    }

    protected Intermediates step7_index(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step7_index");
        Store store = parameters.getStore();
        try {
            IndexCreator indexCreator = new IndexCreator(store);
            Path index = indexCreator.create();
            listener.info("created index : " + index.toString());
        } catch (MaterialstoreException | IOException e) {
            throw new InspectusException(e);
        }
        listener.stepFinished("step7_index");
        return Intermediates.builder(intermediates).build();
    }

    protected boolean isSubprocessjAvailable() {
        String className = "com.kazurayam.subprocessj.Subprocess";
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.getSimpleName().equals("Subprocess");
        } catch (Exception e) {
            logger.info(className + " is not available in the classpath");
            return false;
        }
    }
}
