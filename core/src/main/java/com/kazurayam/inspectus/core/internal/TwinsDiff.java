package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialList;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.QueryOnMetadata;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;

import java.util.Collections;

public abstract class TwinsDiff extends AbstractDiffService {

    @Override
    public Intermediates process(Parameters parameters) throws InspectusException {
        Intermediates im2 = step2_materialize(parameters);
        return step3_reduceTwins(parameters, im2);
    }

    public abstract Intermediates step2_materialize(Parameters parameters)
            throws InspectusException;

    public Intermediates step3_reduceTwins(Parameters parameters,
                                           Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step3_reduceTwins");
        if (!parameters.containsStore()) {
            throw new InspectusException(Parameters.KEY_store + " is not give in the parameters");
        }
        Store store = parameters.getStore();

        if (!parameters.containsJobName()) {
            throw new InspectusException(Parameters.KEY_jobName + " is not given in the parameters");
        }
        JobName jobName = parameters.getJobName();

        if (!intermediates.containsProfileLeft()) {
            throw new InspectusException(Intermediates.KEY_profileLeft + " is not given in the intermediates object");
        }
        String profileLeft = intermediates.getProfileLeft();

        if (!intermediates.containsProfileRight()) {
            throw new InspectusException(Intermediates.KEY_profileRight + " is not given in the intermediates object");
        }
        String profileRight = intermediates.getProfileRight();

        if (!intermediates.containsJobTimestampLeft()) {
            throw new InspectusException(Intermediates.KEY_jobTimestampLeft + " is not given in the intermediates object");
        }
        JobTimestamp jobTimestampLeft = intermediates.getJobTimestampLeft();

        if (!intermediates.containsJobTimestampRight()) {
            throw new InspectusException(Intermediates.KEY_jobTimestampRight + " is not given in the parameters");
        }
        JobTimestamp jobTimestampRight = intermediates.getJobTimestampRight();

        try {
            // get the MaterialList of the left (Production Environment)
            MaterialList left = store.select(jobName, jobTimestampLeft,
                    QueryOnMetadata.builder(
                            Collections.singletonMap("profile", profileLeft)
                    ).build());

            // get the MaterialList of the right (Development Envrionment)
            MaterialList right = store.select(jobName, jobTimestampRight,
                    QueryOnMetadata.builder(
                            Collections.singletonMap("profile", profileRight)
                    ).build());
            SortKeys sortKeys = new SortKeys();

            // weave 2 MaterialList objects into a MaterialProductGroup,
            // which is a List of pairs of corresponding Material
            MaterialProductGroup reduced =
                    MaterialProductGroup.builder(left, right)
                            .ignoreKeys("profile", "URL.host", "URL.port")
                            .sort("step")
                            .build();
            Inspector inspector = Inspector.newInstance(store);
            inspector.setSortKeys(sortKeys);

            // take diff, measure the difference, sort them
            MaterialProductGroup inspected =
                    inspector.reduceAndSort(reduced);

            // we will pass the MaterialProductGroup object to
            // the reporting step that follows
            Intermediates result =
                    new Intermediates.Builder()
                            .materialProductGroup(inspected)
                            .build();

            listener.stepFinished("step3_reduceTwins");
            return result;

        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }
}
