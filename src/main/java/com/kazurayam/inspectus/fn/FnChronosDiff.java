package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.ChronosDiff;

import java.util.function.BiFunction;

public class FnChronosDiff extends ChronosDiff {

    private final BiFunction<Parameters, Intermediates, Intermediates> fn;

    public FnChronosDiff(BiFunction<Parameters, Intermediates, Intermediates> fn) {
        this(fn, Environment.DEFAULT);
    }

    public FnChronosDiff(BiFunction<Parameters, Intermediates, Intermediates> fn,
                         Environment environment) {
        this.environment = environment;
        this.fn = fn;
    }

    /**
     * call the Function
     */
    @Override
    public Intermediates processEnvironment(Parameters params,
                                            Environment env,
                                            Intermediates intermediates) {
        Parameters decorated =
                Parameters.builder(params)
                        .environment(env)
                        .build();
        return fn.apply(decorated, intermediates);
    }

    @Override
    public Intermediates step2_materialize(Parameters parameters,
                                           Intermediates intermediates) {
        return processEnvironment(parameters, environment, intermediates);
    }
}
