package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.TwinsDiff;

import java.util.Objects;

public final class KatalonTwinsDiff extends TwinsDiff implements ITestCaseCaller {

    private String materializeTestCaseName = null;

    public KatalonTwinsDiff(String materializeTestCaseName,
                            Environment environmentLeft,
                            Environment environmentRight) {
        Objects.requireNonNull(materializeTestCaseName);
        Objects.requireNonNull(environmentLeft);
        Objects.requireNonNull(environmentRight);
        this.materializeTestCaseName = materializeTestCaseName;
        this.environmentLeft = environmentLeft;
        this.environmentRight = environmentRight;
    }
    @Override
    public Intermediates step2_materialize(Parameters parameters)
            throws InspectusException {
        listener.stepStarted("step2_materialize");
        if (materializeTestCaseName == null) {
            throw new InspectusException("materializeTestCaseName is not specified");
        }
        Parameters decoratedParameters =
                Parameters.builder(parameters)
                        .environmentLeft(environmentLeft)
                        .environmentRight(environmentRight)
                        .build();
        Intermediates intermediates = callTestCase(materializeTestCaseName, decoratedParameters);
        listener.stepFinished("step2_materialize");
        return intermediates;
    }


}
