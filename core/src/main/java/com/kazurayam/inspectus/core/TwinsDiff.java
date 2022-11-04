package com.kazurayam.inspectus.core;

import java.util.Map;

public class TwinsDiff extends AbstractDiffService {

    @Override
    public Map<String, Object> process(Map<String, Object> parameters) throws InspectusException {
        return step2_materialize(parameters);
    }

    protected Map<String, Object> step2_materialize(Map<String, Object> parameters) throws InspectusException {
        throw new RuntimeException("TODO");
    }
}
