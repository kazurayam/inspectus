package com.kazurayam.inspectus.core;

import com.kazurayam.inspectus.festum.InspectusException;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.MaterialList;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;
import java.nio.file.Path;

import java.util.Collections;
import java.util.Map;

public class Shootings extends AbstractService {

    public Shootings(Map<String, Object> parameters) {
        super(parameters);
    }

    @Override
    public Map<String, Object> process(Map<String, Object> parameters) throws InspectusException {
        return step2_materialize(parameters);
    }

    protected Map<String, Object> step2_materialize(Map<String, Object> parameters) throws InspectusException {
        throw new RuntimeException("TODO");
    }

    @Override
    public void step4_report(Map<String, Object> parameters, Map<String, Object> intermediates)
            throws InspectusException {
        Store store = getStore(parameters);
        JobName jobName = getJobName(parameters);
        Store backup = getBackup(parameters);
        MaterialList materialList = getMaterialList(intermediates);
        SortKeys sortKeys = getSortKeys(intermediates);
        //
        Inspector inspector = Inspector.newInstance(store);
        inspector.setSortKeys(sortKeys);
        try {
            Path report = inspector.report(materialList);
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }
}
