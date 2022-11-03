package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.Festum;
import com.kazurayam.inspectus.core.InspectusException;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class KatalonTestCaseCallerTest {

    @Test
    public void test_smoke() throws InspectusException {
        Festum f = new KatalonTestCaseCaller();
        assertThrows(InspectusException.class, () -> {
            f.call("materialize", Collections.emptyMap());
        });
    }

}
