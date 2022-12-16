package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.ChronosDiff;

import java.util.Objects;

public final class KatalonChronosDiff extends ChronosDiff implements ITestCaseCaller {

    private String materializeTestCaseName = null;

    public KatalonChronosDiff(String materializeTestCaseName) {
        Objects.requireNonNull(materializeTestCaseName);
        this.materializeTestCaseName = materializeTestCaseName;
        super.setListener(new KatalonStepListener());
    }
    @Override
    public Intermediates step2_materialize(Parameters parameters,
                                           Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step2_materialize");
        if (materializeTestCaseName == null) {
            throw new InspectusException("materializeTestCaseName is not specified");
        }
        Intermediates result =
                callTestCase(materializeTestCaseName, parameters, intermediates);
        listener.stepFinished("step2_materialize");
        return result;
    }
}
