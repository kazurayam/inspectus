package com.kazurayam.inspectus.example;

import com.kazurayam.inspectus.core.Festum;
import com.kazurayam.inspectus.core.InspectusException;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloTest {

    @Test
    public void test_call() throws InspectusException {
        Festum hello = new Hello();
        Object obj = hello.call("world", Collections.emptyMap());
        assertTrue(obj instanceof String);
        String msg = (String)obj;
        assertTrue(msg.startsWith("Hello"));
    }
}
