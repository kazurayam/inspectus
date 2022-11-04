package com.kazurayam.inspectus.festum;

import java.util.Map;

public final class PostMaterialize implements PostFestum {

    @Override
    public void postprocess(Map<String, Object> parameters) throws InspectusException {
        System.out.println("Goodbye from " + this.getClass().getSimpleName());
    }
}
