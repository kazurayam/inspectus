package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.Inspectus;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.TestHelper;
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

public class FnShootingsTest {

    private Store store;

    @BeforeEach
    public void beforeEach() throws IOException {
        Path testClassOutputDir = TestHelper.createTestClassOutputDir(FnShootingsTest.class);
        store = Stores.newInstance(testClassOutputDir.resolve("store"));
    }

    @Test
    public void test_smoke() throws InspectusException {
        JobName jobName = new JobName("test_smoke");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        Parameters parameters = new Parameters.Builder()
                .store(store)
                .jobName(jobName)
                .jobTimestamp(jobTimestamp).build();
        // Action
        Inspectus sh = new FnShootings(fn);
        sh.execute(parameters);
    }

    /**
     * Function object that creates a fileTree "store/jobName/jobTimestamp"
     * where 1 text file is written
     */
    private final Function<Parameters, Intermediates> fn = p -> {
        Store st = p.getStore();
        JobName jn = p.getJobName();
        JobTimestamp jt = p.getJobTimestamp();
        Metadata md = new Metadata.Builder().build();
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
