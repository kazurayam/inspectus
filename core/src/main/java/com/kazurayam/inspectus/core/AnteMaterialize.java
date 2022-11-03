package com.kazurayam.inspectus.core;

import java.util.Map;

public final class AnteMaterialize implements AnteFestum {

    @Override
    public void preprocess(Map<String, Object> parameters) throws InspectusException {
        System.out.println("Hello, from " + this.getClass().getSimpleName()+ "!");
    }
}

