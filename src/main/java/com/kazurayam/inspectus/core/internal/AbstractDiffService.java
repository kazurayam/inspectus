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
    public Intermediates step4_report(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step4_report");
        Store store = parameters.getStore();
        MaterialProductGroup materialProductGroup = intermediates.getMaterialProductGroup();
        if ( !materialProductGroup.isReadyToReport()) {
            throw new InspectusException("MaterialProductGroup is not ready to report");
        }
        SortKeys sortKeys = parameters.getSortKeys();
        Double threshold = parameters.getThreshold();
        //
        Inspector inspector = Inspector.newInstance(store);
        inspector.setSortKeys(sortKeys);
        try {
            Path report = inspector.report(materialProductGroup, threshold);
            int warnings = materialProductGroup.countWarnings(threshold);
            listener.stepFinished("step4_report");
            return Intermediates.builder(intermediates)
                    .warnings(warnings)  // report the number of warnings
                    .build();
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }
}
