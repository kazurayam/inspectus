package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.ChronosDiff;

import java.util.Objects;

public final class KatalonChronosDiff extends ChronosDiff implements ITestCaseCaller {

    private String materializeTestCaseName = null;

    public KatalonChronosDiff(String materializeTestCaseName) {
        this(materializeTestCaseName, Environment.DEFAULT);
    }

    public KatalonChronosDiff(String materializeTestCaseName,
                              Environment environment) {
        Objects.requireNonNull(materializeTestCaseName);
        Objects.requireNonNull(environment);
        this.materializeTestCaseName = materializeTestCaseName;
        this.environment = environment;
        super.setListener(new KatalonStepListener());
    }

    /**
     * call the "materialize" Test Case
     */
    @Override
    public Intermediates processEnvironment(Parameters params,
                                            Environment env,
                                            Intermediates intermediates)
            throws InspectusException {
        Parameters decoratedParameters =
                Parameters.builder(params)
                        .environment(env).build();
        return callTestCase(materializeTestCaseName, decoratedParameters, intermediates);
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
                processEnvironment(parameters, environment, intermediates);
        listener.stepFinished("step2_materialize");
        return result;
    }
}
