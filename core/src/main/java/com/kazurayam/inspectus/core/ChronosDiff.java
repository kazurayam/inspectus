package com.kazurayam.inspectus.core;

import com.kazurayam.inspectus.festum.InspectusException;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;

import java.nio.file.Path;
import java.util.Map;

public class ChronosDiff extends AbstractDiffService {

    @Override
    public Map<String, Object> process(Map<String, Object> parameters) throws InspectusException {
        return step2_materialize(parameters);
    }

    protected Map<String, Object> step2_materialize(Map<String, Object> parameters) throws InspectusException {
        throw new RuntimeException("TODO");
    }
}
