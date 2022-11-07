package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.ChronosDiff;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;

import java.util.Objects;

public final class KatalonChronosDiff extends ChronosDiff implements ITestCaseCaller {

    private String materializeTestCaseName = null;

    public KatalonChronosDiff(String materializeTestCaseName) {
        Objects.requireNonNull(materializeTestCaseName);
        this.materializeTestCaseName = materializeTestCaseName;
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
