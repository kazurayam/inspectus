package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.core.filesystem.metadata.IgnoreMetadataKeys;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParametersTest {

    @Test
    public void test_ignoreMetadataKeys() {
        IgnoreMetadataKeys imk = new IgnoreMetadataKeys.Builder()
                .ignoreKey("protocol").build();
        Parameters p = new Parameters.Builder()
                .ignoreMetadataKeys(imk).build();
        assertNotNull(p.getIgnoreMetadataKeys());
        assertTrue(p.getIgnoreMetadataKeys().contains("protocol"));
    }
}
