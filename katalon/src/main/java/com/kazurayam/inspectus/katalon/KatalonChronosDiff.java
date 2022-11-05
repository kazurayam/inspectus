package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.ChronosDiff;
import com.kazurayam.inspectus.core.InspectusException;

import java.util.Map;

public final class KatalonChronosDiff extends ChronosDiff implements ITestCaseCaller {

    @Override
    public Map<String, Object> step2_materialize(Map<String, Object> parameters)
            throws InspectusException {
        String testCaseName = getTestCaseName(parameters);
        return callTestCase(testCaseName, parameters);
    }
}
