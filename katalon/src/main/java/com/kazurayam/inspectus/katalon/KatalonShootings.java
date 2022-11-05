package com.kazurayam.inspectus.katalon;

import com.google.gson.Gson;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.Shootings;
import com.kazurayam.inspectus.util.GsonHelper;
import com.kazurayam.materialstore.core.filesystem.MaterialList;

public final class KatalonShootings extends Shootings implements ITestCaseCaller {

    @Override
    public Intermediates process(Parameters parameters) throws InspectusException {
        Intermediates intermediates = step2_materialize(parameters);
        return intermediates;
    }

    @Override
    public Intermediates step2_materialize(Parameters parameters)
            throws InspectusException {
        listener.stepStarted("step2_materialize");
        String materializeScriptName = parameters.getMaterializeScriptName();
        Intermediates intermediates = callTestCase(materializeScriptName, parameters);
        // The Test Case must return a MaterialList
        if ( intermediates.getMaterialList() == MaterialList.NULL_OBJECT) {
            Gson gson = GsonHelper.createGson(true);
            String json = gson.toJson(intermediates);
            throw new InspectusException(String.format(
                    "Test Case '%s' did not return '%s'. The intermediates returned was %s",
                    materializeScriptName, "materialList", json));
        }
        listener.stepFinished("step2_materialize");
        return intermediates;
    }
}
