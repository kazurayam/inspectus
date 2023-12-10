package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.MaterialList;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.Metadata;
import com.kazurayam.materialstore.core.SortKeys;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.diagram.dot.DotGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.Objects;

public abstract class TwinsDiff extends AbstractDiffService {

    private final Logger logger = LoggerFactory.getLogger(TwinsDiff.class);
    protected Environment environmentLeft = Environment.NULL_OBJECT;
    protected Environment environmentRight = Environment.NULL_OBJECT;

    @Override
    public Intermediates process(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(intermediates);
        if (environmentLeft == null) {
            throw new InspectusException("environmentLeft must not be null");
        }
        if (environmentLeft == Environment.NULL_OBJECT) {
            throw new InspectusException("environmentLeft must be set");
        }
        if (environmentRight == null) {
            throw new InspectusException("environmentRight must not be null");
        }
        if (environmentRight == Environment.NULL_OBJECT) {
            throw new InspectusException("environmentRight must be set");
        }

        Intermediates result2 = step2_materialize(parameters, intermediates);
        assert result2 != null;

        Intermediates stuffedIntermediates =
                Intermediates.builder(result2)
                        .environmentLeft(environmentLeft)
                        .environmentRight(environmentRight)
                        .jobTimestampLeft(result2.getJobTimestampLeft())
                        .jobTimestampRight(result2.getJobTimestampRight())
                        .build();

        return step3_reduceTwins(parameters, stuffedIntermediates);
    }

    @Override
    public Intermediates step2_materialize(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step2_materialize");
        if ( ! parameters.containsJobTimestamp()) {
            throw new InspectusException("jobTimestamp is required");
        }

        // take the screenshots of the left environment
        // while passing the given JobTimestamp to the custom code
        JobTimestamp jobTimestampLeft = parameters.getJobTimestamp();
        Intermediates resultLeft =
                processEnvironment(parameters, environmentLeft, intermediates);
        assert resultLeft != null;

        // take the screenshots of the right environment
        // while passing a calculated JobTimestamp to the custom code
        JobTimestamp jobTimestampRight = JobTimestamp.laterThan(jobTimestampLeft);
        Parameters modifiedParams =
                Parameters.builder(parameters)
                        .jobTimestamp(jobTimestampRight).build();
        Intermediates resultRight =
                processEnvironment(modifiedParams, environmentRight, resultLeft);
        assert resultRight != null;

        listener.stepFinished("step2_materialize");

        return Intermediates.builder(resultRight)
                .jobTimestampLeft(jobTimestampLeft)
                .jobTimestampRight(jobTimestampRight)
                .build();
    }

    public abstract Intermediates processEnvironment(Parameters params,
                                                     Environment env,
                                                     Intermediates intermediates)
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
            MaterialList left = store.select(jobName, jobTimestampLeft);

            logger.debug("llllllllllllll MaterialList left llllllllllllllllllll\n" +
                    left.toJson(true) +
                    "llllllllllllllllllll");

            // get the MaterialList of the right (Development Environment)
            MaterialList right = store.select(jobName, jobTimestampRight);

            logger.debug("rrrrrrrrrrrrrr + MaterialList right rrrrrrrrrrrrrrrrrr\n" +
                    right.toJson(true) +
                    "rrrrrrrrrrrrrrrrrrrr");

            // weave 2 MaterialList objects into a MaterialProductGroup,
            // which is a List of pairs of corresponding Material

            MaterialProductGroup reduced =
                    MaterialProductGroup.builder(left, right)
                            .ignoreKeys("environment", "URL.host")
                            .ignoreKeys(parameters.getIgnoreMetadataKeys())
                            .labelLeft(environmentLeft.getValue())
                            .labelRight(environmentRight.getValue())
                            .build();

            logger.debug("============= MaterialProductGroup just after built ==========================\n" +
                    reduced.toJson(true) +
                    "=========================================");

            // logger.info("parameters.getIgnoreMetadataKeys=" + parameters.getIgnoreMetadataKeys().toString());
            // logger.info("reduced.getIgnoreMetadataKeys=" + reduced.getIgnoreMetadataKeys().toString());

            Inspector inspector = Inspector.newInstance(store);
            inspector.setSortKeys(sortKeys);

            // take diff, measure the difference, sort them
            MaterialProductGroup inspected;
            try {

                inspected = inspector.reduceAndSort(reduced);

                logger.debug("+++++++++++++ MaterialProductGroup after inspector.reduceAndSort() ++++++++++++++++\n"+
                        inspected.toJson(true) +
                        "++++++++++++++++++++++++++++++++++++++++++++");
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
                    Intermediates.builder(intermediates)
                            .materialProductGroup(inspected)
                            .build();

            listener.stepFinished("step3_reduceTwins");
            return result;

        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }
}
