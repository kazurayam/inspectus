package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.TestHelper;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.fn.FnChronosDiff;
import com.kazurayam.materialstore.core.filesystem.FileType;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.Metadata;
import com.kazurayam.materialstore.core.filesystem.Store;
import com.kazurayam.materialstore.core.filesystem.Stores;
import com.kazurayam.materialstore.core.util.DeleteDir;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChronosDiffTest {

    private static Path testCaseOutputDir;
    private static Store store;
    private static Store storeBackup;

    @BeforeAll
    public static void beforeAll() {
        testCaseOutputDir = TestHelper.createTestClassOutputDir(ChronosDiffTest.class);
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        Path storeRoot = testCaseOutputDir.resolve("store");
        Path storeBackupRoot = testCaseOutputDir.resolve("store-backup");
        if (Files.exists(storeRoot)) {
            DeleteDir.deleteDirectoryRecursively(storeRoot);
        }
        if (Files.exists(storeBackupRoot)) {
            DeleteDir.deleteDirectoryRecursively(storeBackupRoot);
        }
        store = Stores.newInstance(storeRoot);
        storeBackup = Stores.newInstance(storeBackupRoot);
    }

    /*
     * write a text "Hello" into the store as test fixture
     */
    private void writeFixture(Store store, JobName jobName, JobTimestamp jobTimestamp)
            throws MaterialstoreException {
        store.write(jobName, jobTimestamp, FileType.TXT, Metadata.NULL_OBJECT, "Hello");
    }

    /*
     * this Function does nothing
     */
    private final BiFunction<Parameters, Intermediates, Intermediates> fn =
            (parameters, intermediates) -> {
        return Intermediates.builder(intermediates).build();
    };

    @Test
    public void test_step1_restorePrevious_ordinary_case() throws MaterialstoreException, InspectusException {
        JobName jobName = new JobName("test_step1_restorePrevious_ordinary_case");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        writeFixture(storeBackup, jobName, jobTimestamp);
        Parameters parameters =
                Parameters.builder()
                        .store(store)
                        .backup(storeBackup)
                        .jobName(jobName)
                        .jobTimestamp(jobTimestamp)
                        .build();
        ChronosDiff chronosDiff = new FnChronosDiff(fn);
        Intermediates result = chronosDiff.process(parameters, Intermediates.NULL_OBJECT);
    }

    @Test
    public void test_step1_restorePrevious_when_storeBack_dir_is_not_there() {
        JobName jobName = new JobName("test_step1_restorePrevious_when_storeBack_dir_is_not_there");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        // we do not create the storeBackup
        Parameters parameters =
                Parameters.builder()
                        .store(store)
                        .backup(storeBackup)
                        .jobName(jobName)
                        .jobTimestamp(jobTimestamp)
                        .build();
        ChronosDiff chronosDiff = new FnChronosDiff(fn);
        assertDoesNotThrow(() -> chronosDiff.process(parameters, Intermediates.NULL_OBJECT));
    }

    @Test
    public void test_step1_restorePrevious_when_storeBack_dir_is_there_but_without_JobName() throws MaterialstoreException {
        JobName jobName = new JobName("test_step1_restorePrevious_when_storeBack_dir_is_there_but_without_JobName");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        writeFixture(storeBackup, jobName, jobTimestamp);
        Parameters parameters =
                Parameters.builder()
                        .store(store)
                        .backup(storeBackup)
                        .jobName(new JobName("CURA"))  // how silly
                        .jobTimestamp(jobTimestamp)
                        .build();
        ChronosDiff chronosDiff = new FnChronosDiff(fn);
        assertDoesNotThrow(() -> chronosDiff.process(parameters, Intermediates.NULL_OBJECT));
    }
}
