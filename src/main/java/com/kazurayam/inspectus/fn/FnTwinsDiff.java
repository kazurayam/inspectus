package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.TwinsDiff;

import java.util.Objects;
import java.util.function.BiFunction;

public class FnTwinsDiff extends TwinsDiff {

    private final BiFunction<Parameters, Intermediates, Intermediates> fn;

    public FnTwinsDiff(BiFunction<Parameters, Intermediates, Intermediates> fn,
                       Environment environmentLeft,
                       Environment environmentRight) {
        Objects.requireNonNull(fn);
        Objects.requireNonNull(environmentLeft);
        Objects.requireNonNull(environmentRight);
        this.fn = fn;
        this.environmentLeft = environmentLeft;
        this.environmentRight = environmentRight;
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

}
