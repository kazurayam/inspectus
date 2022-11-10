package com.kazurayam.inspectus.fn;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.internal.TwinsDiff;

import java.util.function.Function;

public class FnTwinsDiff extends TwinsDiff {

    private Function<Parameters, Intermediates> fn;

    public FnTwinsDiff(Function<Parameters, Intermediates> fn) { this.fn = fn; }

    @Override
    public Intermediates step2_materialize(Parameters parameters) throws InspectusException {
        return fn.apply(parameters);
    }
}
