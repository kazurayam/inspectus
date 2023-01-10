package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.TestFixtureSupport;
import com.kazurayam.inspectus.TestHelper;
import com.kazurayam.inspectus.core.Inspectus;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.UncheckedInspectusException;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.MaterialList;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.Metadata;
import com.kazurayam.materialstore.core.SortKeys;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.core.Stores;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FnChronosDiffTest {

    Logger logger = LoggerFactory.getLogger(FnChronosDiffTest.class);

    private Path baseDir;
    private Store store;

    @BeforeEach
    public void beforeEach() throws IOException {
        baseDir = TestHelper.getCWD();
        Path testClassOutputDir = TestHelper.createTestClassOutputDir(FnChronosDiffTest.class);
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
            Intermediates result1 = fnChronosDiff.execute(parameters1);
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
        Intermediates result2 = fnChronosDiff.execute(parameters2);

        // Assert
        try {
            assertTrue(store.contains(jobName, jobTimestamp1));
            assertTrue(store.contains(jobName, jobTimestamp2));
            assertTrue(result2.getWarnings() > 0);
            logger.info("result2.getWarnings()=" + result2.getWarnings());
        } catch (MaterialstoreException e) {
            throw new UncheckedInspectusException(e);
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
                .jobTimestamp(jt1)
                .build();
        try {
            Inspectus fnChronosDiff = new FnChronosDiff(fn);
            Intermediates result1 = fnChronosDiff.execute(p1);
        } catch (Exception e) {
            System.out.println("1st execution failed. We will continue");
        }
        // 2nd execution
        Thread.sleep(3000);
        JobTimestamp jt2 = JobTimestamp.now();
        Parameters p2 = Parameters.builder()
                .baseDir(baseDir).store(store).jobName(jobName)
                .jobTimestamp(jt2)
                .build();
        Inspectus fnChronosDiff = new FnChronosDiff(fn);
        Intermediates result2 = fnChronosDiff.execute(p2);

        // 3rd execution
        Thread.sleep(3000);
        JobTimestamp jt3 = JobTimestamp.now();
        Parameters p3 = Parameters.builder()
                .baseDir(baseDir).store(store).jobName(jobName)
                .jobTimestamp(jt3)
                .baselinePriorToOrEqualTo(jt1)  // compare the jt3 against the jt1
                .cleanOlderThan(jt1)
                .threshold(10.0D)
                .build();
        Intermediates result3 = fnChronosDiff.execute(p3);

        // assert
        assertTrue(store.contains(jobName, jt1));
        assertTrue(store.contains(jobName, jt3));
        //
        JobTimestamp jtLatest = store.findLatestJobTimestamp(jobName);
        MaterialList ml = store.select(jobName, jtLatest);
        ml.forEach ( material -> {
            assertTrue(material.getMetadata().containsCategoryDiff());
            assertEquals(jt1, material.getMetadata().getMaterialLocatorLeft().getJobTimestamp());
            assertEquals(jt3, material.getMetadata().getMaterialLocatorRight().getJobTimestamp());
        });
        // threshold=10.0D is so big that no warnings will be detected
        assertTrue(result3.getWarnings() == 0);
    }

    @Test
    public void test_createAppleImage() throws IOException, MaterialstoreException {
        JobName jobName = new JobName("test_createAppleImage");
        JobTimestamp jt = JobTimestamp.now();
        Path images = TestHelper.getFixturesDirectory().resolve("images");
        Path apple = images.resolve("apple.png");
        BufferedImage baseImage = ImageIO.read(apple.toFile());
        BufferedImage clockImage = TestFixtureSupport.createAppleImage(jt);
        Material material = store.write(jobName, jt, FileType.PNG, Metadata.NULL_OBJECT, clockImage);
        assertNotNull(material);
        assertEquals(FileType.PNG, material.getFileType());
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
    private final BiFunction<Parameters, Intermediates, Intermediates> fn =
            (parameters, intermediates) -> {
        Path bd = parameters.getBaseDir();
        Path images = bd.resolve("src/test/fixtures/images");
        Path mikan = images.resolve("mikan.png");
        Path money = images.resolve("money.png");
        Store st = parameters.getStore();
        JobName jn = parameters.getJobName();
        JobTimestamp jt = parameters.getJobTimestamp();
        try {
            BufferedImage appleWithTimestamp = TestFixtureSupport.createAppleImage(jt);
            st.write(jn, jt, FileType.PNG,
                    Metadata.builder().put("imageOf", "apple").build(),
                    appleWithTimestamp);
            st.write(jn, jt, FileType.PNG, Metadata.builder()
                    .put("imageOf", "orange")
                    .build(), mikan);
            st.write(jn, jt, FileType.PNG, Metadata.builder()
                    .put("imageOf", "cash")
                    .build(), money);
        } catch (MaterialstoreException | IOException e) {
            throw new RuntimeException(e);
        }
        return Intermediates.builder(intermediates).build();
    };
}













