package com.kazurayam.inspectus.core;

public interface Inspectus {
    default void execute(Parameters parameters) throws InspectusException {
        // ante festum
        preProcess(parameters);
        // in festum
        Intermediates intermediates = process(parameters);
        // post festum
        postProcess(parameters, intermediates);
    }

    void preProcess(Parameters parameters) throws InspectusException;

    Intermediates process(Parameters parameters) throws InspectusException;

    void postProcess(Parameters parameters, Intermediates intermediates) throws InspectusException;
}

