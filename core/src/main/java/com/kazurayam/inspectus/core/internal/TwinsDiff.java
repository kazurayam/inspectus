package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.filesystem.FileType;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.Material;
import com.kazurayam.materialstore.core.filesystem.MaterialList;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.Metadata;
import com.kazurayam.materialstore.core.filesystem.QueryOnMetadata;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;
import com.kazurayam.materialstore.core.filesystem.metadata.IgnoreMetadataKeys;
import com.kazurayam.materialstore.diagram.dot.DotGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;

public abstract class TwinsDiff extends AbstractDiffService {

    private Logger logger = LoggerFactory.getLogger(TwinsDiff.class);

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

        if (!parameters.containsProfileLeft()) {
            throw new InspectusException(Parameters.KEY_profileLeft + " is not given in the parameters");
        }

        if (!parameters.containsProfileRight()) {
            throw new InspectusException(Parameters.KEY_profileRight + " is not given in the parameters");
        }

        SortKeys sortKeys = parameters.getSortKeys();

        //------------ values passed through Intermediates object --------------------------

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
                            Collections.singletonMap("profile", parameters.getProfileLeft())
                    ).build());

            // get the MaterialList of the right (Development Envrionment)
            MaterialList right = store.select(jobName, jobTimestampRight,
                    QueryOnMetadata.builder(
                            Collections.singletonMap("profile", parameters.getProfileRight())
                    ).build());

            // weave 2 MaterialList objects into a MaterialProductGroup,
            // which is a List of pairs of corresponding Material


            MaterialProductGroup reduced =
                    MaterialProductGroup.builder(left, right)
                            .ignoreKeys("profile", "URL.host")
                            .ignoreKeys(parameters.getIgnoreMetadataKeys())
                            .build();

            // logger.info("parameters.getIgnoreMetadataKeys=" + parameters.getIgnoreMetadataKeys().toString());
            // logger.info("reduced.getIgnoreMetadataKeys=" + reduced.getIgnoreMetadataKeys().toString());

            Inspector inspector = Inspector.newInstance(store);
            inspector.setSortKeys(sortKeys);

            // take diff, measure the difference, sort them
            MaterialProductGroup inspected;
            try {
                inspected = inspector.reduceAndSort(reduced);
            } catch (MaterialstoreException e) {
                /*
                 * if any problem occurred while taking diff,
                 * create a diagram of the MaterialProductGroup object
                 * and store it in a new JobTimestamp for debugging.
                 */
                /* before calling the DotGenerator.generateDot() we will check if
                 * the com.kazurayam.subprocessj.Subprocess class is available
                 * in the classpath as DotGenerator depends on the GraphViz dot command.
                 * If not, skip generating the diagram.
                 */
                if (isSubprocessjAvailable()) {

                    String dotText = DotGenerator.generateDot(reduced);
                    //                                        ^^^^^^^
                    BufferedImage bi = DotGenerator.toImage(dotText);
                    JobTimestamp jt = JobTimestamp.now();
                    Material dotMat =
                            store.write(jobName, jt, FileType.PNG,
                                    Metadata.NULL_OBJECT, bi);
                    logger.info(String.format("look at %s/%s for the diagram of MaterialProductGroup: %s",
                            jobName, jt, reduced.getShortID()));
                }
                // rethrow it
                throw e;
            }

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
