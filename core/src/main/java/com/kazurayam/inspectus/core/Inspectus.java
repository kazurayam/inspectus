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

    public void preProcess(Parameters parameters) throws InspectusException;
    public Intermediates process(Parameters parameters) throws InspectusException;
    public void postProcess(Parameters parameters, Intermediates intermediates) throws InspectusException;
}

