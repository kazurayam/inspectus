package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.Environment;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.TwinsDiff;

import java.util.Objects;
import java.util.function.Function;

public class FnTwinsDiff extends TwinsDiff {

    private Function<Parameters, Intermediates> fn;

    public FnTwinsDiff(Function<Parameters, Intermediates> fn,
                       Environment environmentLeft,
                       Environment environmentRight) {
        Objects.requireNonNull(fn);
        Objects.requireNonNull(environmentLeft);
        Objects.requireNonNull(environmentRight);
        this.fn = fn;
        this.environmentLeft = environmentLeft;
        this.environmentRight = environmentRight;
    }

    @Override
    public Intermediates step2_materialize(Parameters parameters) throws InspectusException {
        return fn.apply(parameters);
    }
}
