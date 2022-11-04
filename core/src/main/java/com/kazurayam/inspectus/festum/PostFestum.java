package com.kazurayam.inspectus.festum;

import java.util.Map;

public interface PostFestum {

    public void postprocess(Map<String, Object> parameters) throws InspectusException;

}

