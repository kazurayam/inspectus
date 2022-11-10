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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FnChronosDiffTest {

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
        SortKeys sortKeys = new SortKeys("imageOf");
        JobTimestamp jobTimestamp1 = JobTimestamp.now();
        Parameters parameters1 = new Parameters.Builder()
                .baseDir(baseDir).store(store).jobName(jobName)
                .jobTimestamp(jobTimestamp1)
                .sortKeys(sortKeys)
                .build();
        // Action
        // 1st time execution
        try {
            Inspectus fnChronosDiff = new FnChronosDiff(fn);
            fnChronosDiff.execute(parameters1);
        } catch (Exception e) {
            System.out.println("1st execution failed. We will continue.");
        }

        // intentionally insert a small time gap
        try {
            Thread.sleep(1000);
        } catch (Exception e) {}

        // 2nd time execution
        JobTimestamp jobTimestamp2 = JobTimestamp.now();
        Parameters parameters2 = new Parameters.Builder()
                .baseDir(baseDir).store(store).jobName(jobName)
                .jobTimestamp(jobTimestamp2)
                .sortKeys(sortKeys)
                .build();
        Inspectus fnChronosDiff = new FnChronosDiff(fn);
        fnChronosDiff.execute(parameters2);

        // Assert
        try {
            assertTrue(store.contains(jobName, jobTimestamp1));
            assertTrue(store.contains(jobName, jobTimestamp2));
        } catch (MaterialstoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Function object that create a fileTree "store/jobName/jobTimestamp",
     * each of which contains 3 PNG images.
     *
     * The image of apple is randomly selected amongst a red one and a green one
     * based on the current timestamp;
     * if the second is odd then choose the red image,
     * if the second is even then choose the green image.
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
        JobTimestamp jt = p.getJobTimestamp();
        try {
            // 1st time of shooting
            int second = jt.value().getSecond();
            if (second % 2 == 1) {
                st.write(jn, jt, FileType.PNG, Metadata.builder()
                        .put("imageOf", "apple")
                        .build(), apple);
            } else {
                st.write(jn, jt, FileType.PNG, Metadata.builder()
                        .put("imageOf", "apple")
                        .build(), greenApple);
            }
            st.write(jn, jt, FileType.PNG, Metadata.builder()
                    .put("imageOf", "orange")
                    .build(), mikan);
            st.write(jn, jt, FileType.PNG, Metadata.builder()
                    .put("imageOf", "cash")
                    .build(), money);
        } catch (MaterialstoreException e) {
            throw new RuntimeException(e);
        }
        return Intermediates.NULL_OBJECT;
    };
}













