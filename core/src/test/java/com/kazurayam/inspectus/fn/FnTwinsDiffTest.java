package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.Inspectus;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.TestHelper;
import com.kazurayam.materialstore.core.filesystem.FileType;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
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
        SortKeys sortKeys = new SortKeys("imageOf");
        Parameters parameters = new Parameters.Builder()
                .baseDir(baseDir).store(store).jobName(jobName)
                .jobTimestamp(jobTimestamp)
                .sortKeys(sortKeys)
                .build();
        // Action
        Inspectus fnTwinsDiff = new FnTwinsDiff(fn, "ProductionEnv", "DevelopmentEnv");
        fnTwinsDiff.execute(parameters);
        // Assert
        try {
            assertTrue(store.contains(jobName, jobTimestamp));
        } catch (MaterialstoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Function object that create 2 fileTree "store/jobName/leftJobTimestamp"
     * and "store/jobName/RightJobTimestamp", each of which contains 3 PNG images
     * files.
     */
    private Function<Parameters, Intermediates> fn = p -> {
        Path bd = p.getBaseDir();
        Path images = bd.resolve("src/test/fixtures/images");
        Path apple = images.resolve("apple.png");
        Path greenApple = images.resolve("green-apple.png");
        Path mikan = images.resolve("mikan.png");
        Path money = images.resolve("money.png");
        Store st = p.getStore();
        JobName jn = p.getJobName();
        JobTimestamp jt1 = p.getJobTimestamp();
        JobTimestamp jt2 = JobTimestamp.laterThan(jt1);
        try {
            // 1st set of shooting
            st.write(jn, jt1, FileType.PNG, Metadata.builder()
                    .put("profile", p.getProfileLeft())
                    .put("imageOf", "apple")
                    .build(), apple);
            st.write(jn, jt1, FileType.PNG, Metadata.builder()
                    .put("profile", p.getProfileLeft())
                    .put("imageOf", "orange")
                    .build(), mikan);
            st.write(jn, jt1, FileType.PNG, Metadata.builder()
                    .put("profile", p.getProfileLeft())
                    .put("imageOf", "cash")
                    .build(), money);
            // 2nd set of shooting
            st.write(jn, jt2, FileType.PNG, Metadata.builder()
                    .put("profile", p.getProfileRight())
                    .put("imageOf", "apple")
                    .build(), greenApple);
            st.write(jn, jt2, FileType.PNG, Metadata.builder()
                    .put("profile", p.getProfileRight())
                    .put("imageOf", "orange")
                    .build(), mikan);
            st.write(jn, jt2, FileType.PNG, Metadata.builder()
                    .put("profile", p.getProfileRight())
                    .put("imageOf", "cash")
                    .build(), money);
        } catch (MaterialstoreException e) {
            throw new RuntimeException(e);
        }
        return new Intermediates.Builder()
                .jobTimestampLeft(jt1)
                .jobTimestampRight(jt2)
                .build();
    };

}
