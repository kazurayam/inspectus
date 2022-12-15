package com.kazurayam.inspectus.core;

public interface Inspectus {

    default Intermediates execute(Parameters parameters) throws InspectusException {
        // ante festum
        Intermediates preProcessResult = preProcess(parameters);
        // in festum
        Intermediates processResult = process(parameters, preProcessResult);
        // post festum
        return postProcess(parameters, processResult);
    }

    Intermediates preProcess(Parameters parameters) throws InspectusException;

    Intermediates process(Parameters parameters, Intermediates intermediates) throws InspectusException;

    Intermediates postProcess(Parameters parameters, Intermediates intermediates) throws InspectusException;
}

