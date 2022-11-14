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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractService implements Inspectus {

    private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    protected StepListener listener;

    public AbstractService() {
        listener = new StdStepListener();
    }

    /**
     * import the lastest artifacts from the remote/backup store
     * into the local/current store
     * @param parameters must contain the keys of "store", "back" and "jobName"
     * @throws InspectusException
     */
    @Override
    public final void preProcess(Parameters parameters) throws InspectusException {
        step0_restorePrevious(parameters);
    }

    @Override
    public void postProcess(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        step4_report(parameters, intermediates);

        step5_backupLatest(parameters, intermediates);

        step6_cleanup(parameters, intermediates);

        step7_index(parameters, intermediates);
    }

    protected final void step0_restorePrevious(Parameters parameters)
            throws InspectusException {
        listener.stepStarted("step0_restorePrevious");
        Store backup = parameters.getBackup();
        Store store = parameters.getStore();
        JobName jobName = parameters.getJobName();
        try {
            if (backup.contains(jobName)) {
                StoreImport importer = StoreImport.newInstance(backup, store);
                importer.importReports(jobName);
            } else {
                logger.warn(String.format("JobName %s is not found in the backup store %s",
                        jobName, backup.toString()));
            }
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
        listener.stepFinished("step0_restorePrevious");
    }


    abstract protected void step4_report(Parameters parameters, Intermediates intermediates)
            throws InspectusException;

    protected void step5_backupLatest(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step5_backupLatest");
        Store store = parameters.getStore();
        JobName jobName = parameters.getJobName();
        Store backup = parameters.getBackup();
        try {
            StoreExport export = StoreExport.newInstance(store, backup);
            export.exportReports(jobName);
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
        listener.stepFinished("step5_backupLatest");
    }

    protected void step6_cleanup(Parameters parameters, Intermediates intermediates)
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
    }

    protected void step7_index(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step7_index");
        Store store = parameters.getStore();
        try {
            IndexCreator indexCreator = new IndexCreator(store);
            indexCreator.create();
        } catch (MaterialstoreException | IOException e) {
            throw new InspectusException(e);
        }
        listener.stepFinished("step7_index");
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
