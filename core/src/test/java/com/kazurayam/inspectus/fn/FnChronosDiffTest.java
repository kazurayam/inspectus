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
     * In this test, we will execute FnChronosDiff 3 times.
     * The 1st execution is to create a baseline JobTimestamp.
     * The 2nd execution is to create the latest previous JobTimestamp.
     * As for the 3rd execution, we will specify the "baselinePriorTo" in the Parameters
     * to set (the 1st JobTimestamp + 1 second) as the baseline.
     * We will assert the 3rd JobTimestamp is compared against the 1st JobTimestamp,
     * rather than the 2nd JobTimestamp.
     */
    @Test
    public void test_baselinePriorTo() throws InterruptedException, InspectusException, MaterialstoreException {
        // setup
        JobName jobName = new JobName("test_baselinePriorTo");
        // 1st execution
        JobTimestamp jt1 = JobTimestamp.now();
        Parameters p1 = Parameters.builder()
                .baseDir(baseDir).store(store).jobName(jobName)
                .jobTimestamp(jt1).build();
        try {
            Inspectus fnChronosDiff = new FnChronosDiff(fn);
            fnChronosDiff.execute(p1);
        } catch (Exception e) {
            System.out.println("1st execution failed. We will continue");
        }
        // 2nd execution
        Thread.sleep(3000);
        JobTimestamp jt2 = JobTimestamp.now();
        Parameters p2 = Parameters.builder()
                .baseDir(baseDir).store(store).jobName(jobName)
                .jobTimestamp(jt2)
                .cleanOlderThan(jt1)   // we will retain the jt1
                .build();
        Inspectus fnChronosDiff = new FnChronosDiff(fn);
        fnChronosDiff.execute(p2);
        // 3rd execution
        Thread.sleep(3000);
        JobTimestamp jt3 = JobTimestamp.now();
        Parameters p3 = Parameters.builder()
                .baseDir(baseDir).store(store).jobName(jobName)
                .jobTimestamp(jt3)
                .baselinePriorTo(jt1.plusSeconds(1))  // compare the jt3 against the jt1
                .cleanOlderThan(jt1)
                .build();
        fnChronosDiff.execute(p3);
        // assert
        assertTrue(store.contains(jobName, jt1));
        assertTrue(store.contains(jobName, jt3));
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













