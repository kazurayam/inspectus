package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.ChronosDiff;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;

public final class KatalonChronosDiff extends ChronosDiff implements ITestCaseCaller {

    @Override
    public Intermediates step2_materialize(Parameters parameters)
            throws InspectusException {
        listener.stepStarted("step2_materialize");
        if (!parameters.containsMaterializeScriptName()) {
            throw new InspectusException("materializeScriptName is not specified");
        }
        String materializeScriptName = parameters.getMaterializeScriptName();
        Intermediates intermediates = callTestCase(materializeScriptName, parameters);
        listener.stepFinished("step2_materialize");
        return intermediates;
    }
}
