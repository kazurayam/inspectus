package com.kazurayam.inspectus.materialize.discovery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SitemapLoader {

    private final Target baseTopPage;
    private final Target twinTopPage;

    public SitemapLoader() {
        this(Target.NULL_OBJECT, Target.NULL_OBJECT);
    }

    public SitemapLoader(Target baseTopPage) {
        this(baseTopPage, Target.NULL_OBJECT);
    }

    public SitemapLoader(Target baseTopPage, Target twinTopPage) {
        this.baseTopPage = baseTopPage;
        this.twinTopPage = twinTopPage;
    }

    public Sitemap parseJson(String jsonText) throws MaterialstoreException {
        JsonElement jsonElement = new JsonParser().parse(jsonText);
        JsonObject jo = jsonElement.getAsJsonObject();
        Target baseTopPage = Target.deserialize(jo.getAsJsonObject("baseTopPage"));
        Target twinTopPage = Target.deserialize(jo.getAsJsonObject("twinTopPage"));
        JsonArray ja = jo.getAsJsonArray("targetList");
        Sitemap sitemap = new Sitemap(baseTopPage, twinTopPage);
        for (JsonElement je : ja) {
            JsonObject tjo = je.getAsJsonObject();
            Target te = Target.deserialize(tjo);
            sitemap.add(te);
        }
        return sitemap;
    }

    public Sitemap parseJson(Path jsonPath) throws MaterialstoreException {
        return this.parseJson(readFully(jsonPath));
    }

    public Sitemap parseJson(File jsonFile) throws MaterialstoreException {
        return this.parseJson(jsonFile.toPath());
    }

    public Sitemap parseCSV(String csvText) throws MaterialstoreException {
        Objects.requireNonNull(csvText);
        if (baseTopPage == Target.NULL_OBJECT) {
            throw new IllegalArgumentException("baseTopPage is required but not set");
        }
        Reader in = new StringReader(csvText);
        CSVFormat csvFormat =
                CSVFormat.RFC4180.builder().setHeader()
                        .setSkipHeaderRecord(true).build();
        Sitemap sitemap = new Sitemap(baseTopPage, twinTopPage);
        try {
            Iterable<CSVRecord> records = csvFormat.parse(in);
            for (CSVRecord record : records) {
                Map<String,String> map = record.toMap();
                if (map.get("url") != null &&
                        map.get("url").length() > 0) {
                    URL url = resolveUrl(map.get("url"));
                    Handle handle = Handle.deserialize(map.get("handle"));
                    map.remove("url");
                    map.remove("handle");
                    Target t =
                            Target.builder(url).handle(handle)
                                    .putAll(map).build();
                    sitemap.add(t);
                }
            }
        } catch (IOException e) {
            throw new MaterialstoreException(e);
        }
        return sitemap;
    }

    public Sitemap parseCSV(Path csvPath) throws MaterialstoreException {
        return this.parseCSV(readFully(csvPath));
    }

    public Sitemap parseCSV(File csvFile) throws MaterialstoreException {
        return this.parseCSV(csvFile.toPath());
    }

    private String readFully(Path p) throws MaterialstoreException {
        try {
            List<String> lines = Files.readAllLines(p);
            return String.join("\n", lines);
        } catch (IOException e) {
            throw new MaterialstoreException(e);
        }
    }

    /**
     * urlString could be in 2 forms:
     * - with URL protocol <PRE>http://hostname/foo.html</PRE>
     * - without URL protocol <PRE>/foo.html</PRE>
     * Either form should be accepted.
     * When the protocol is omitted, then the baseTopPage is refered to
     * supplement the protocol+host+port of the url to return
     *
     * the baseTopPage is
     * @param urlSpec e.g, "http://host/foo.html" or "/foo.html" or "foo.html"
     * @return URL
     */
    public URL resolveUrl(String urlSpec) throws MaterialstoreException {
        Objects.requireNonNull(urlSpec);
        try {
            URL url1 = new URL(urlSpec);
            return url1;
        } catch (MalformedURLException e1) {
            if (e1.getMessage().contains("no protocol")) {
                try {
                    URL url2 = new URL(baseTopPage.getUrl(), urlSpec);
                    return url2;
                } catch (MalformedURLException e2) {
                    throw new MaterialstoreException(e2);
                }
            } else {
                throw new MaterialstoreException("urlStr=\"" + urlSpec + "\"");
            }
        }
    }
}
