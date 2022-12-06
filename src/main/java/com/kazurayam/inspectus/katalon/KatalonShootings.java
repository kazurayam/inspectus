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
    public Intermediates process(Parameters parameters) throws InspectusException {
        return step2_materialize(parameters);
    }

    @Override
    public Intermediates step2_materialize(Parameters parameters)
            throws InspectusException {
        listener.stepStarted("step2_materialize");
        if (materializeTestCaseName == null) {
            throw new InspectusException("materializeTestCaseName is not specified");
        }
        Intermediates intermediates = callTestCase(materializeTestCaseName, parameters);
        listener.stepFinished("step2_materialize");
        return intermediates;
    }
}
