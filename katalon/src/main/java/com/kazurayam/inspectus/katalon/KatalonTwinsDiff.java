package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.TwinsDiff;

import java.util.Map;

public final class KatalonTwinsDiff extends TwinsDiff implements ITestCaseCaller {

    @Override
    public Map<String, Object> step2_materialize(Map<String, Object> parameters)
            throws InspectusException {
        String testCaseName = getTestCaseName(parameters);
        return callTestCase(testCaseName, parameters);
    }
}
