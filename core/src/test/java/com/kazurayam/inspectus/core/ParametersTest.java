package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.metadata.IgnoreMetadataKeys;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void test_baselinePriorTo() {
        LocalDateTime ldt = LocalDateTime.of(2022, 11, 14, 9, 46, 20);
        JobTimestamp baseline = JobTimestamp.create(ldt);
        Parameters p = new Parameters.Builder().baselinePriorTo(baseline).build();
        assertNotNull(p.getBaselinePriorTo());
        assertEquals(baseline, p.getBaselinePriorTo());
    }

    @Test
    public void test_baselinePriorToOrEqualTo() {
        LocalDateTime ldt = LocalDateTime.of(2022, 11, 14, 9, 46, 20);
        JobTimestamp baseline = JobTimestamp.create(ldt);
        Parameters p = new Parameters.Builder().baselinePriorToOrEqualTo(baseline).build();
        assertNotNull(p.getBaselinePriorTo());
        assertEquals(baseline.plusSeconds(1), p.getBaselinePriorTo());
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
