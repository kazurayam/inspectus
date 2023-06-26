package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.base.reduce.Reducer;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobNameNotFoundException;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.MaterialList;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.SortKeys;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.diagram.dot.MPGVisualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class ChronosDiff extends AbstractDiffService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Boolean isDiagramRequired = false;

    protected Environment environment = Environment.NULL_OBJECT;

    protected void isDiagramRequired(Boolean required) {
        this.isDiagramRequired = required;
    }

    @Override
    public Intermediates process(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(intermediates);
        if (environment == null) {
            throw new InspectusException("environment must not be null");
        }
        if (environment == Environment.NULL_OBJECT) {
            throw new InspectusException("environment must be set");
        }

        Intermediates result2 = step2_materialize(parameters, intermediates);
        assert result2 != null;

        Intermediates stuffedIntermediates =
                Intermediates.builder(result2)
                        .environmentLeft(environment)
                        .environmentRight(environment)
                        .jobTimestampLeft(result2.getJobTimestampLeft())
                        .jobTimestampRight(result2.getJobTimestampRight())
                        .build();

        return step3_reduceChronos(parameters, stuffedIntermediates);
    }

    public abstract Intermediates processEnvironment(Parameters params,
                                                     Environment env,
                                                     Intermediates intermediates)
        throws InspectusException;


    public Intermediates step3_reduceChronos(Parameters parameters,
                                    Intermediates intermediates) throws InspectusException {
        listener.stepStarted("step3_reduceChronos");
        Store store = parameters.getStore();
        JobName jobName = parameters.getJobName();
        JobTimestamp jobTimestamp = parameters.getJobTimestamp();
        SortKeys sortKeys = parameters.getSortKeys();
        try {
            listener.info("going to select the current MaterialList");
            MaterialList currentMaterialList = store.select(jobName, jobTimestamp);
            //
            MaterialProductGroup reduced;
            if (parameters.containsBaselinePriorTo()) {
                listener.info("will compare the current JobTimestamp against the baseline specified as parameter");
                reduced = Reducer.chronos(store, currentMaterialList,
                        parameters.getBaselinePriorTo());
            } else {
                listener.info("going to take the previous-latest JobTimestamp as the baseline to compare the current one against");
                reduced = Reducer.chronos(store, currentMaterialList);
            }
            //
            Inspector inspector = Inspector.newInstance(store);
            inspector.setSortKeys(sortKeys);
            listener.info("going to call inspector.reduceAndSort(reduced)");
            MaterialProductGroup inspected =
                    inspector.reduceAndSort(reduced);
            if (isDiagramRequired && inspected.getNumberOfBachelors() > 0) {
                listener.info("going to call MPGVisualizer to draw a diagram of MaterialProductGroup");
                // if any bachelor found, generate diagram of MaterialProductGroup object
                MPGVisualizer visualizer = new MPGVisualizer(store);
                visualizer.visualize(inspected.getJobName(),
                        JobTimestamp.now(), inspected);
            }
            listener.stepFinished("step3_reduceChronos");
            return new Intermediates.Builder().materialProductGroup(inspected).build();
        } catch (MaterialstoreException | JobNameNotFoundException e) {
            throw new InspectusException(e);
        }
    }

}
