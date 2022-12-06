package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.TestHelper;
import com.kazurayam.inspectus.fn.FnShootings;
import com.kazurayam.materialstore.core.filesystem.FileType;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialList;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.Metadata;
import com.kazurayam.materialstore.core.filesystem.Store;
import com.kazurayam.materialstore.core.filesystem.Stores;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractServiceTest {

    private Path testClassOutputDir;
    private Store store;

    @BeforeEach
    public void beforeEach() throws IOException {
        testClassOutputDir = TestHelper.createTestClassOutputDir(this);
        store = Stores.newInstance(testClassOutputDir.resolve("store"));
    }

    /**
     * test if the "cleanOlderThan" parameter is properly accepted and used by
     * FnShootings class. This test will indirectly ensure the AbstractService class is
     * implemented to deal with the parameter as intended.
     */
    @Test
    public void test_parameters_cleanOlderThan() throws InterruptedException, InspectusException, MaterialstoreException {
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

    private Function<Parameters, Intermediates> fn = p -> {
        Store st = p.getStore();
        JobName jn = p.getJobName();
        JobTimestamp jt = p.getJobTimestamp();
        Metadata md = Metadata.builder().build();
        try {
            st.write(jn, jt, FileType.TXT, md, "Hello, world!");
            MaterialList ml = st.select(jn, jt);
            Intermediates im = new Intermediates.Builder()
                    .materialList(ml).build();
            return im;
        } catch (MaterialstoreException e) {
            throw new RuntimeException(e);
        }
    };
}
