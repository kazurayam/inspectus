package com.kazurayam.inspectus.core.internal;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.materialstore.base.inspector.Inspector;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.MaterialList;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.SortKeys;
import com.kazurayam.materialstore.core.Store;

import java.nio.file.Path;
import java.util.Objects;

public abstract class Shootings extends AbstractService {

    public Shootings() { super(); }

    @Override
    public Intermediates process(Parameters parameters, Intermediates intermediates)
            throws InspectusException {
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(intermediates);
        return step2_materialize(parameters, intermediates);
    }

    public abstract Intermediates step2_materialize(Parameters parameters,
                                                    Intermediates intermediates)
            throws InspectusException;

    @Override
    public Intermediates step4_report(Parameters parameters,
                                      Intermediates intermediates)
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
        return Intermediates.builder(intermediates).build();
    }
}
