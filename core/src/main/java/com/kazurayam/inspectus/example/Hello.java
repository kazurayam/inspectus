package com.kazurayam.inspectus.example;

import com.google.gson.Gson;
import com.kazurayam.inspectus.util.GsonHelper;

import java.util.Map;

public class Hello implements Festum {

    public Hello() {}

    @Override
    public Object call(String name, Map<String, Object> binding) throws InspectusException {
        return "Hello, " + name + " with param: " + toJson(binding);
    }

    private String toJson(Map<String, Object> binding) {
        Map<String, String> m = GsonHelper.toStringStringMap(binding);
        Gson gson = GsonHelper.createGson(false);
        return gson.toJson(m);
    }
}
