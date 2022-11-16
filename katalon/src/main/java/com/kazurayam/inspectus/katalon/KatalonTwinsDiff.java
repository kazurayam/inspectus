package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.TwinsDiff;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;

import java.util.Objects;

public final class KatalonTwinsDiff extends TwinsDiff implements ITestCaseCaller {

    private final String materializeTestCaseName;

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


    /**
     * call the "materialize" Test Case
     */
    @Override
    public Intermediates processEnvironment(Parameters params, Environment env) throws InspectusException {
        Parameters decoratedParameters =
                Parameters.builder(params)
                        .environment(env).build();
        return callTestCase(materializeTestCaseName, decoratedParameters);
    }

}
