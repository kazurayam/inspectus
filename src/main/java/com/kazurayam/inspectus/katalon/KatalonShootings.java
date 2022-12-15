package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.Shootings;

import java.util.Objects;

public final class KatalonShootings extends Shootings implements ITestCaseCaller {

    private String materializeTestCaseName = null;

    public KatalonShootings(String materializeTestCaseName) {
        Objects.requireNonNull(materializeTestCaseName);
        this.materializeTestCaseName = materializeTestCaseName;
    }

    @Override
    public Intermediates process(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        return step2_materialize(parameters, intermediates);
    }

    @Override
    public Intermediates step2_materialize(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step2_materialize");
        if (materializeTestCaseName == null) {
            throw new InspectusException("materializeTestCaseName is not specified");
        }
        Intermediates result = callTestCase(materializeTestCaseName, parameters, intermediates);
        listener.stepFinished("step2_materialize");
        return result;
    }
}
