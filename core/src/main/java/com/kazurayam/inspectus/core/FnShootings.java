package com.kazurayam.inspectus.core;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.Shootings;
import java.util.function.Function;

public final class FnShootings extends Shootings {

    private Function<Parameters, Intermediates> fn;

    public FnShootings(Function<Parameters, Intermediates> fn) {
        this.fn = fn;
    }
    @Override
    public Intermediates step2_materialize(Parameters parameters) throws InspectusException {
        return fn.apply(parameters);
    }
}
