package com.kazurayam.inspectus.festum;

import java.util.Map;

public class PostNihil implements PostFestum {
    @Override
    public void postprocess(Map<String, Object> parameters) throws InspectusException {
        // does nothing
    }
}
