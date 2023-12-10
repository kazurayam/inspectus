package com.kazurayam.inspectus.materialize.discovery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kazurayam.inspectus.core.InspectusException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SitemapLoader {

    private static final Logger logger = LoggerFactory.getLogger(SitemapLoader.class);

    private SitemapLoader() {}



    public static Sitemap parseSitemapCSV(String csvText, boolean withHeaderRecord, Map<String, String> bindings) throws InspectusException {
        Objects.requireNonNull(csvText);
        Objects.requireNonNull(bindings);
        // ${URL_PREFIX} => https://hostname etc
        String text = interpolateString(csvText, bindings);
        //
        Reader in = new StringReader(text);
        Sitemap sitemap = new Sitemap();
        if (withHeaderRecord) {
            CSVFormat csvFormat =
                    CSVFormat.RFC4180.builder().setHeader()
                            .setCommentMarker('#')
                            .setSkipHeaderRecord(true).build();

            try {
                Iterable<CSVRecord> records = csvFormat.parse(in);
                for (CSVRecord record: records) {
                    Map<String, String> map = record.toMap();
                    if (map.get("url") != null && map.get("url").length() > 0) {
                        URL url = new URL(map.get("url"));
                        if (record.size() > 1) {
                            Handle handle = Handle.deserialize(record.get(1));
                            sitemap.add(Target.builder(url).handle(handle).build());
                        } else {
                            sitemap.add(Target.builder(url).build());
                        }
                    } else {
                        // ignore blank lines
                    }
                }
                if (sitemap.size() == 0) {
                    logger.warn("parseSitemapCSV() withHeaderRecord=true returned an empty Sitemap ");
                }
            } catch (IOException e) {
                throw new InspectusException(e);
            }
        } else {
            CSVFormat csvFormat =
                    CSVFormat.Builder.create().setCommentMarker('#').build();
            try {
                Iterable<CSVRecord> records = csvFormat.parse(in);
                for (CSVRecord record : records) {
                    if (record.get(0) != null && record.get(0).length() > 0) {
                        URL url = new URL(record.get(0));
                        if (record.size() > 1) {
                            Handle handle = Handle.deserialize(record.get(1));
                            sitemap.add(Target.builder(url).handle(handle).build());
                        } else {
                            sitemap.add(Target.builder(url).build());
                        }
                    }
                }
                if (sitemap.size() == 0) {
                    logger.warn("parseCSV() withHeaderRecord=false returned an empty Sitemap");
                }
            } catch (IOException e) {
                throw new InspectusException(e);
            }
        }
        return sitemap;
    }

    public static Sitemap parseSitemapCSV(String csvText) throws InspectusException {
        return parseSitemapCSV(csvText, true, new HashMap<String, String>());
    }

    public static Sitemap parseSitemapCSV(String csvText, Map<String, String> bindings)
        throws InspectusException {
        return parseSitemapCSV(csvText, true, bindings);
    }

    public static Sitemap parseSitemapCSV(String csvText, boolean withHeaderRecord)
            throws InspectusException {
        return parseSitemapCSV(csvText, withHeaderRecord, new HashMap<String, String>());
    }

    public static Sitemap parseSitemapJson(String jsonText, Map<String, String> bindings) throws InspectusException {
        Objects.requireNonNull(jsonText);
        Objects.requireNonNull(bindings);
        String text = interpolateString(jsonText, bindings);
        JsonElement jsonElement = JsonParser.parseString(text);
        JsonObject jo = jsonElement.getAsJsonObject();
        Sitemap sitemap = new Sitemap();
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

    public static Sitemap parseSitemapJson(String jsonText) throws InspectusException {
        return parseSitemapJson(jsonText, new HashMap<String, String>());
    }

    public static Sitemap loadSitemapJson(Path jsonPath) throws InspectusException {
        return loadSitemapJson(jsonPath, new HashMap<String, String>());
    }

    public static Sitemap loadSitemapJson(Path jsonPath, Map<String, String> bindings) throws InspectusException {
        return parseSitemapJson(readFully(jsonPath), bindings);
    }

    public static String interpolateString(String baseString, Map<String, String> bindings) {
        StringSubstitutor substitutor = new StringSubstitutor(bindings);
        return substitutor.replace(baseString);
    }

    public static String readFully(Path p) throws InspectusException {
        try {
            List<String> lines = Files.readAllLines(p);
            return String.join("\n", lines);
        } catch (IOException e) {
            throw new InspectusException(e);
        }
    }

}
