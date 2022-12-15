package com.kazurayam.inspectus.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntermediatesTest {

    @Test
    public void test_carrying_warnings() throws InspectusException {
        Intermediates seed = Intermediates.NULL_OBJECT;
        assertEquals(0, seed.getWarnings());
        Intermediates base = Intermediates.builder(seed).warnings(999).build();
        assertEquals(999, base.getWarnings());
        Intermediates pass = Intermediates.builder(base).build();
        assertEquals(999, pass.getWarnings());
    }
}
