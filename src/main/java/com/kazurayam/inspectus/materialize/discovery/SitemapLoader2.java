package com.kazurayam.inspectus.materialize.discovery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kazurayam.inspectus.core.InspectusException;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SitemapLoader2 {

    private static final Logger logger = LoggerFactory.getLogger(SitemapLoader.class);

    private SitemapLoader2() {}


    public static Sitemap2 parseSitemapJson(String jsonText, Map<String, String> bindings) throws InspectusException {
        String text = interpolateString(jsonText, bindings);
        JsonElement jsonElement = JsonParser.parseString(text);
        JsonObject jo = jsonElement.getAsJsonObject();
        Sitemap2 sitemap = new Sitemap2();
        JsonObject topPageObject = jo.getAsJsonObject("topPage");
        if (topPageObject != null) {
            sitemap.setBaseUrl(Target.deserialize(topPageObject));
        }
        JsonArray ja = jo.getAsJsonArray("targetList");
        for (JsonElement je : ja) {
            JsonObject tjo = je.getAsJsonObject();
            Target te = Target.deserialize(tjo);
            sitemap.add(te);
        }
        return sitemap;
    }

    public static Sitemap2 parseSitemapJson(String jsonText) throws InspectusException {
        return parseSitemapJson(jsonText, new HashMap<String, String>());
    }

    public static Sitemap2 loadSitemapJson(Path jsonPath) throws InspectusException {
        return loadSitemapJson(jsonPath, new HashMap<String, String>());
    }

    public static Sitemap2 loadSitemapJson(Path jsonPath, Map<String, String> bindings) throws InspectusException {
        return parseSitemapJson(readFully(jsonPath), bindings);
    }

    static String interpolateString(String baseString, Map<String, String> bindings) {
        StringSubstitutor substitutor = new StringSubstitutor(bindings);
        return substitutor.replace(baseString);
    }

    static String readFully(Path p) throws InspectusException {
        try {
            List<String> lines = Files.readAllLines(p);
            return String.join("\n", lines);
        } catch (IOException e) {
            throw new InspectusException(e);
        }
    }

}
