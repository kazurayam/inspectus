package com.kazurayam.inspectus.core;

import com.kazurayam.inspectus.festum.InspectusException;

import java.util.Map;

public interface Inspectus {

    default void execute(Map<String, Object> parameters) throws InspectusException {
        // ante festum
        preProcess(parameters);
        // in festum
        Map<String, Object> intermediates = process(parameters);
        // post festum
        postProcess(parameters, intermediates);
    }

    public void preProcess(Map<String, Object> parameters) throws InspectusException;
    public Map<String, Object> process(Map<String, Object> parameters) throws InspectusException;
    public void postProcess(Map<String, Object> parameters, Map<String, Object> intermediates) throws InspectusException;
}

