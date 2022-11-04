package com.kazurayam.inspectus.core;

import com.kazurayam.inspectus.festum.InspectusException;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;

import java.nio.file.Path;
import java.util.Map;

public abstract class AbstractDiffService extends AbstractService {

    @Override
    public void step4_report(Map<String, Object> parameters, Map<String, Object> intermediates)
            throws InspectusException {
        Store store = getStore(parameters);
        MaterialProductGroup materialProductGroup = getMaterialProductGroup(intermediates);
        if ( !materialProductGroup.isReadyToReport()) {
            throw new InspectusException(AbstractService.KEY_MaterialProductGroup + " is not ready to report");
        }
        SortKeys sortKeys = getSortKeys(intermediates);
        Double criteria = getCriteria(intermediates);
        //
        Inspector inspector = Inspector.newInstance(store);
        inspector.setSortKeys(sortKeys);
        try {
            Path report = inspector.report(materialProductGroup, criteria);
            int warnings = materialProductGroup.countWarnings(criteria);
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }
}
