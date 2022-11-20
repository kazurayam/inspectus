package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.Inspectus;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.TestHelper;
import com.kazurayam.materialstore.core.filesystem.FileType;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialList;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.Metadata;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;
import com.kazurayam.materialstore.core.filesystem.Stores;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FnTwinsDiffTest {

    private Path baseDir;
    private Path testClassOutputDir;
    private Store store;

    @BeforeEach
    public void beforeEach() throws IOException {
        baseDir = TestHelper.getCWD();
        testClassOutputDir = TestHelper.createTestClassOutputDir(this);
        store = Stores.newInstance(testClassOutputDir.resolve("store"));
    }

    @Test
    public void test_smoke() throws InspectusException {
        JobName jobName = new JobName("test_smoke");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        Parameters parameters = new Parameters.Builder()
                .baseDir(baseDir).store(store).jobName(jobName)
                .jobTimestamp(jobTimestamp)
                .sortKeys(new SortKeys("imageOf"))
                .build();
        // Action
        Inspectus fnTwinsDiff =
                new FnTwinsDiff(fn,
                        new Environment("ProductionEnv"),
                        new Environment("DevelopmentEnv"));
        fnTwinsDiff.execute(parameters);

        // Assert
        try {
            assertTrue(store.contains(jobName, jobTimestamp));
            assertNotNull(store.selectSingle(jobName, jobTimestamp));
            assertTrue(store.selectSingle(jobName, jobTimestamp).getMetadata()
                    .containsKey(Parameters.KEY_environment));
            assertEquals("ProductionEnv",
                    store.selectSingle(jobName, jobTimestamp).getMetadata()
                            .get(Parameters.KEY_environment));
            //
        } catch (MaterialstoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Function object that create 2 fileTree "store/jobName/leftJobTimestamp"
     * and "store/jobName/RightJobTimestamp", each of which contains 3 PNG images
     * files.
     */
    private final Function<Parameters, Intermediates> fn = p -> {
        Path bd = p.getBaseDir();
        Store st = p.getStore();
        JobName jn = p.getJobName();
        JobTimestamp jt = p.getJobTimestamp();
        Environment env = p.getEnvironment();

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

            return new Intermediates.Builder()
                    .materialList(mt)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}
