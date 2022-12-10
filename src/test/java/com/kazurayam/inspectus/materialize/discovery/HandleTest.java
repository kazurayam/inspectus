package com.kazurayam.inspectus.materialize.discovery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HandleTest {

    @Test
    public void test_deserialize_cssSelector() {
        String s = "By.cssSelector: #main";
        Handle h = Handle.deserialize(s);
        assertNotNull(h);
        assertEquals(s, h.getBy().toString());
    }

    @Test
    public void test_deserialize_xpath() {
        String s = "By.xpath: //*[@id='main']";
        Handle h = Handle.deserialize(s);
        assertNotNull(h);
        assertEquals(s, h.getBy().toString());
    }
}
