package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialList;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;

import java.nio.file.Path;

public abstract class Shootings extends AbstractService {

    public Shootings() { super(); }

    @Override
    public Intermediates process(Parameters parameters) throws InspectusException {
        Intermediates intermediates = step2_materialize(parameters);
        return intermediates;
    }

    public abstract Intermediates step2_materialize(Parameters parameters) throws InspectusException;

    @Override
    public void step4_report(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        listener.stepStarted("step4_report");
        Store store = parameters.getStore();
        JobName jobName = parameters.getJobName();
        JobTimestamp jobTimestamp = parameters.getJobTimestamp();
        //
        try {
            MaterialList materialList = store.select(jobName, jobTimestamp);
            SortKeys sortKeys = parameters.getSortKeys();
            //
            Inspector inspector = Inspector.newInstance(store);
            inspector.setSortKeys(sortKeys);
            Path report = inspector.report(materialList);
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
        listener.stepFinished("step4_report");
    }
}
