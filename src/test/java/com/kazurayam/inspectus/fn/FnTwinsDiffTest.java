package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.Inspectus;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.TestHelper;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobNameNotFoundException;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.MaterialList;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.Metadata;
import com.kazurayam.materialstore.core.SortKeys;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.core.Stores;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FnTwinsDiffTest {

    private Path baseDir;
    private Store store;

    @BeforeEach
    public void beforeEach() throws IOException {
        baseDir = TestHelper.getCWD();
        Path testClassOutputDir = TestHelper.createTestClassOutputDir(FnTwinsDiffTest.class);
        store = Stores.newInstance(testClassOutputDir.resolve("store"));
    }

    @Test
    public void test_storingImageFiles()
            throws InspectusException, JobNameNotFoundException {
        JobName jobName = new JobName("test_storingImageFiles");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        Parameters parameters = Parameters.builder()
                .baseDir(baseDir)
                .store(store)
                .jobName(jobName)
                .jobTimestamp(jobTimestamp)
                .sortKeys(new SortKeys("imageOf"))
                .build();
        // Action
        Inspectus fnTwinsDiff =
                new FnTwinsDiff(fn,
                        new Environment("ProductionEnv"),
                        new Environment("DevelopmentEnv"));
        Intermediates result = fnTwinsDiff.execute(parameters);

        // Assert
        try {
            assertTrue(store.contains(jobName, jobTimestamp));
            assertNotNull(store.selectSingle(jobName, jobTimestamp));
            assertTrue(store.selectSingle(jobName, jobTimestamp).getMetadata()
                    .containsKey(Parameters.KEY_environment));
            assertEquals("ProductionEnv",
                    store.selectSingle(jobName, jobTimestamp).getMetadata()
                            .get(Parameters.KEY_environment));
            assertTrue(result.getWarnings() > 0);
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }

    /**
     * Function object that create 2 fileTree "store/jobName/leftJobTimestamp"
     * and "store/jobName/RightJobTimestamp", each of which contains 3 PNG images
     * files.
     */
    private final BiFunction<Parameters, Intermediates, Intermediates> fn =
            (parameters, intermediates) -> {
        Path bd = parameters.getBaseDir();
        Store st = parameters.getStore();
        JobName jn = parameters.getJobName();
        JobTimestamp jt = parameters.getJobTimestamp();
        Environment env = parameters.getEnvironment();

        Path images = bd.resolve("src/test/fixtures/images");
        // the apple could be red or green randomly
        Path apple = (jt.value().getSecond() %2 == 1) ?
                images.resolve("apple.png") :
                images.resolve("green-apple.png");
        Path mikan = images.resolve("mikan.png");
        Path money = images.resolve("money.png");

        try {
            st.write(jn, jt, FileType.PNG,
                    Metadata.builder()
                            .put("environment", env.toString())
                            .put("imageOf", "apple")
                            .build(), apple);
            st.write(jn, jt, FileType.PNG,
                    Metadata.builder()
                            .put("environment", env.toString())
                            .put("imageOf", "orange")
                            .build(), mikan);
            st.write(jn, jt, FileType.PNG,
                    Metadata.builder()
                            .put("environment", env.toString())
                            .put("imageOf", "cash")
                            .build(), money);

            MaterialList mt = store.select(jn, jt);

            return Intermediates.builder(intermediates)
                    .materialList(mt)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}
