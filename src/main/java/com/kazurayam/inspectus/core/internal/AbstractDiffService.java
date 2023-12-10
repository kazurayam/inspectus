package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.SortKeys;
import com.kazurayam.materialstore.core.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public abstract class AbstractDiffService extends AbstractService {

    private Logger logger = LoggerFactory.getLogger(AbstractDiffService.class);

    @Override
    public Intermediates step4_report(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step4_report");
        Store store = parameters.getStore();
        MaterialProductGroup materialProductGroup = intermediates.getMaterialProductGroup();

        logger.debug("*********************************\n" +
                materialProductGroup.toJson(true) +
                "********************************");

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
