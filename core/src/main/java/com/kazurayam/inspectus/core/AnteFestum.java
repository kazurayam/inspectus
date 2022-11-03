package com.kazurayam.inspectus.core;

import java.util.Map;

public interface AnteFestum {

    public void preprocess(Map<String, Object> parameters) throws InspectusException;

}

