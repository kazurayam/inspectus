package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.manage.StoreImport;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.base.reduce.Reducer;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.MaterialList;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.SortKeys;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.diagram.dot.MPGVisualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.util.Objects;

public abstract class ChronosDiff extends AbstractDiffService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Boolean isDiagramRequired = false;

    protected void isDiagramRequired(Boolean required) {
        this.isDiagramRequired = required;
    }

    @Override
    public Intermediates process(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(intermediates);
        Intermediates result1 = step1_restorePrevious(parameters, intermediates);
        Intermediates result2 = step2_materialize(parameters, result1);
        return step3_reduceChronos(parameters, result2);
    }

    public Intermediates step1_restorePrevious(Parameters parameters,
                                               Intermediates intermediates)
            throws InspectusException {
        Store backup = parameters.getBackup();
        if ( ! Files.exists(backup.getRoot()) ) {
            logger.warn(backup.getRoot() + " is not found");
            logger.info("Possibly this is the first time you ran this test. Will be OK next time. Try again.");
        } else {
            Store store = parameters.getStore();
            JobName jobName = parameters.getJobName();
            try {
                StoreImport importer = StoreImport.newInstance(backup, store);
                importer.importReports(jobName);
            } catch (MaterialstoreException e) {
                if (e.getMessage().contains(
                        String.format("JobName \"%s\" is not found in %s", jobName, backup))) {
                    logger.warn(e.getMessage());
                    logger.info("This warning may happen. You should try again.");
                } else {
                    throw new InspectusException(e);
                }
            }
        }
        return Intermediates.builder(intermediates).build();
    }

    public abstract Intermediates step2_materialize(Parameters parameters, Intermediates intermediates)
            throws InspectusException;

    public Intermediates step3_reduceChronos(Parameters parameters,
                                    Intermediates intermediates) throws InspectusException {
        listener.stepStarted("step3_reduceChronos");
        Store store = parameters.getStore();
        JobName jobName = parameters.getJobName();
        JobTimestamp jobTimestamp = parameters.getJobTimestamp();
        SortKeys sortKeys = parameters.getSortKeys();
        try {
            MaterialList currentMaterialList = store.select(jobName, jobTimestamp);
            //
            MaterialProductGroup reduced;
            if (parameters.containsBaselinePriorTo()) {
                logger.info("will compare the current JobTimestamp against the baseline specified as parameter");
                reduced = Reducer.chronos(store, currentMaterialList,
                        parameters.getBaselinePriorTo());
            } else {
                logger.info("will take the previous-latest JobTimestamp as the baseline to compare the current one against");
                reduced = Reducer.chronos(store, currentMaterialList);
            }
            //
            Inspector inspector = Inspector.newInstance(store);
            inspector.setSortKeys(sortKeys);
            MaterialProductGroup inspected =
                    inspector.reduceAndSort(reduced);
            if (isDiagramRequired && inspected.getNumberOfBachelors() > 0) {
                // if any bachelor found, generate diagram of MaterialProductGroup object
                MPGVisualizer visualizer = new MPGVisualizer(store);
                visualizer.visualize(inspected.getJobName(),
                        JobTimestamp.now(), inspected);
            }
            listener.stepFinished("step3_reduceChronos");
            return new Intermediates.Builder().materialProductGroup(inspected).build();
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }

}
