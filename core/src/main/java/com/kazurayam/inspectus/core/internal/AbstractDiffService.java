package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;

import java.nio.file.Path;

public abstract class AbstractDiffService extends AbstractService {

    @Override
    public void step4_report(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step4_report");
        Store store = parameters.getStore();
        MaterialProductGroup materialProductGroup = intermediates.getMaterialProductGroup();
        if ( !materialProductGroup.isReadyToReport()) {
            throw new InspectusException("MaterialProductGroup is not ready to report");
        }
        SortKeys sortKeys = parameters.getSortKeys();
        Double criteria = parameters.getCriteria();
        //
        Inspector inspector = Inspector.newInstance(store);
        inspector.setSortKeys(sortKeys);
        try {
            Path report = inspector.report(materialProductGroup, criteria);
            int warnings = materialProductGroup.countWarnings(criteria);
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
        listener.stepFinished("step4_report");
    }
}
