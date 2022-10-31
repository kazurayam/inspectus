package com.kazurayam.inspectus.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    public void test_execute() {
        Main instance = new Main();
        String msg = instance.execute("Inspectus");
        assertEquals("Hello, Inspectus!", msg);
    }
}
