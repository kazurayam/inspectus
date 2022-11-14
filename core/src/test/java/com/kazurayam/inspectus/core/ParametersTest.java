package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
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

    @Test
    public void test_cleanOlderThan() {
        JobTimestamp olderThan = JobTimestamp.now().minusMinutes(30);
        Parameters p = new Parameters.Builder().cleanOlderThan(olderThan).build();
        assertNotNull(p.getCleanOlderThan());
        assertTrue(p.getCleanOlderThan().compareTo(
                JobTimestamp.now().minusMinutes(30)) <= 0);
        assertTrue(p.getCleanOlderThan().compareTo(
                JobTimestamp.now().minusMinutes(31)) > 0);
    }
}
