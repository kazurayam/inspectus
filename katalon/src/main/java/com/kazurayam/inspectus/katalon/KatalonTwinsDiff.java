package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.TwinsDiff;

import java.util.Objects;

public final class KatalonTwinsDiff extends TwinsDiff implements ITestCaseCaller {

    private String materializeTestCaseName = null;

    public KatalonTwinsDiff(String materializeTestCaseName,
                            String profileLeft,
                            String profileRight) {
        Objects.requireNonNull(materializeTestCaseName);
        Objects.requireNonNull(profileLeft);
        Objects.requireNonNull(profileRight);
        this.materializeTestCaseName = materializeTestCaseName;
        this.profileLeft = profileLeft;
        this.profileRight = profileRight;
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
