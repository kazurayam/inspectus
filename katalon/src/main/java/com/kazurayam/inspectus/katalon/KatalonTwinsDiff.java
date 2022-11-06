package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.TwinsDiff;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialList;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.QueryOnMetadata;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;

import java.util.Collections;

public final class KatalonTwinsDiff extends TwinsDiff implements ITestCaseCaller {

    @Override
    public Intermediates step2_materialize(Parameters parameters)
            throws InspectusException {
        listener.stepStarted("step2_materialize");
        if (!parameters.containsMaterializeScriptName()) {
            throw new InspectusException("materializeScriptName is not specified");
        }
        String materializeScriptName = parameters.getMaterializeScriptName();
        Intermediates intermediates = callTestCase(materializeScriptName, parameters);
        listener.stepFinished("step2_materialize");
        return intermediates;
    }


}
