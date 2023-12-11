package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.fn.FnShootings;
import com.kazurayam.inspectus.zest.TestOutputOrganizerFactory;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobNameNotFoundException;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.MaterialList;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractServiceTest {

    private static final TestOutputOrganizer too =
            TestOutputOrganizerFactory.create(AbstractServiceTest.class);
    private static Store store;

    @BeforeAll
    public static void beforeAll() throws IOException {
        too.cleanClassOutputDirectory();
        Path testClassOutputDir = too.getClassOutputDirectory();
        store = Stores.newInstance(testClassOutputDir.resolve("store"));
    }

    /**
     * test if the "cleanOlderThan" parameter is properly accepted and used by
     * FnShootings class. This test will indirectly ensure the AbstractService class is
     * implemented to deal with the parameter as intended.
     */
    @Test
    public void test_parameters_cleanOlderThan()
            throws InterruptedException, InspectusException,
            MaterialstoreException, JobNameNotFoundException {
        // setup
        JobName jobName = new JobName("test_parameters_cleanOlderThan");
        JobTimestamp ts1 = JobTimestamp.now();
        // 1st execution to make a target, which will be cleaned by the 2nd execution
        Parameters p1 = new Parameters.Builder()
                .store(store).jobName(jobName).jobTimestamp(ts1)
                .build();
        new FnShootings(fn).execute(p1);
        assertTrue(store.contains(jobName, ts1));
        // wait for 3 seconds
        Thread.sleep(3000);
        // 2nd execution to clean up the target
        JobTimestamp ts2 = JobTimestamp.now();
        Parameters p2 = new Parameters.Builder()
                .store(store).jobName(jobName).jobTimestamp(ts2)
                .cleanOlderThan(ts2.minusSeconds(2))
                .build();
        // the following execution should clean up the directory
        new FnShootings(fn).execute(p2);
        // assert that the jobName/ts1 directory is removed
        assertFalse(store.contains(jobName, ts1));
    }

    private BiFunction<Parameters, Intermediates, Intermediates> fn = (parameters, intermediates) -> {
        Store st = parameters.getStore();
        JobName jn = parameters.getJobName();
        JobTimestamp jt = parameters.getJobTimestamp();
        Metadata md = Metadata.builder().build();
        try {
            st.write(jn, jt, FileType.TXT, md, "Hello, world!");
            MaterialList ml = st.select(jn, jt);
            Intermediates result = Intermediates.builder(intermediates)
                    .materialList(ml).build();
            return result;
        } catch (MaterialstoreException e) {
            throw new RuntimeException(e);
        }
    };
}
