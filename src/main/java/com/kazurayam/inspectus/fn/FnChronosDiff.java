package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.ChronosDiff;

import java.util.function.BiFunction;

public class FnChronosDiff extends ChronosDiff {

    private BiFunction<Parameters, Intermediates, Intermediates> fn;

    public FnChronosDiff(BiFunction<Parameters, Intermediates, Intermediates> fn) { this.fn = fn; }

    @Override
    public Intermediates step2_materialize(Parameters parameters,
                                           Intermediates intermediates)
            throws InspectusException {
        return fn.apply(parameters, intermediates);
    }
}
