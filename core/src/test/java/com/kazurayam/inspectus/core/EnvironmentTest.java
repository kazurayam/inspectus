package com.kazurayam.inspectus.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvironmentTest {

    @Test
    public void test_getValue() {
        Environment prod = new Environment("ProductionEnv");
        assertEquals("ProductionEnv", prod.getValue());
        assertEquals("ProductionEnv", prod.toString());
    }

    @Test
    public void test_compareTo() {
        Environment dev = new Environment("Dev");
        Environment prod = new Environment("Prod");
        assertTrue(dev.compareTo(prod) < 0);
        assertEquals(0, dev.compareTo(dev));
        assertTrue(prod.compareTo(dev) > 0);
    }
}
