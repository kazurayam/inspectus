package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.TwinsDiff;

public final class KatalonTwinsDiff extends TwinsDiff implements ITestCaseCaller {

    @Override
    public Intermediates step2_materialize(Parameters parameters)
            throws InspectusException {
        listener.stepStarted("step2_materialize");
        String materializeScriptName = parameters.getMaterializeScriptName();
        Intermediates intermediates = callTestCase(materializeScriptName, parameters);
        listener.stepFinished("step2_materialize");
        return intermediates;
    }
}
