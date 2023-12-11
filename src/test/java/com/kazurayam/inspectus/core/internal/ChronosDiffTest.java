package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.fn.FnChronosDiff;
import com.kazurayam.inspectus.zest.TestOutputOrganizerFactory;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.Metadata;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.core.Stores;
import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ChronosDiffTest {

    private static final TestOutputOrganizer too =
            TestOutputOrganizerFactory.create(ChronosDiffTest.class);
    private static Store store;
    private static Store storeBackup;

    @BeforeAll
    public static void beforeAll() throws IOException {
        too.cleanClassOutputDirectory();
        Path testCaseOutputDir = too.getClassOutputDirectory();
        Path storeRoot = testCaseOutputDir.resolve("store");
        Path storeBackupRoot = testCaseOutputDir.resolve("store-backup");
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
