package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.ChronosDiff;

import java.util.function.Function;

public class FnChronosDiff extends ChronosDiff {

    private Function<Parameters, Intermediates> fn;

    public FnChronosDiff(Function<Parameters, Intermediates> fn) { this.fn = fn; }

    @Override
    public Intermediates step2_materialize(Parameters parameters) throws InspectusException {
        return fn.apply(parameters);
    }
}
