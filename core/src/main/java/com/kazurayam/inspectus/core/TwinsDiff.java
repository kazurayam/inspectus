package com.kazurayam.inspectus.core;

public abstract class TwinsDiff extends AbstractDiffService {

    @Override
    public Intermediates process(Parameters parameters) throws InspectusException {
        return step2_materialize(parameters);
    }

    public abstract Intermediates step2_materialize(Parameters parameters) throws InspectusException;

}
