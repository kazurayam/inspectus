package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.Inspectus;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.StdStepListener;
import com.kazurayam.inspectus.zest.TestOutputOrganizerFactory;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
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

public class FnShootingsTest {

    private static final TestOutputOrganizer too =
            TestOutputOrganizerFactory.create(FnShootingsTest.class);
    private static Store store;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Path storePath = too.cleanClassOutputDirectory().resolve("store");
        store = Stores.newInstance(storePath);
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
        sh.setListener(new StdStepListener());
        sh.execute(parameters);
    }

    /**
     * Function object that creates a fileTree "store/jobName/jobTimestamp"
     * where 1 text file is written
     */
    private final BiFunction<Parameters, Intermediates, Intermediates> fn =
            (parameters, intermediates) -> {
        Store st = parameters.getStore();
        JobName jn = parameters.getJobName();
        JobTimestamp jt = parameters.getJobTimestamp();
        Metadata md = new Metadata.Builder().build();
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
