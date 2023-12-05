package com.kazurayam.inspectus.materialize.discovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Sitemap2 implements Iterable<Target>{

    private final Logger logger = LoggerFactory.getLogger(Sitemap2.class);
    private Target baseUrl = null;
    private List<Target> targetList = new ArrayList<>();

    public Sitemap2() {}

    public void setBaseUrl(Target baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Target getBaseUrl() {
        return this.baseUrl;
    }

    public void add(Target target) {
        Objects.requireNonNull(target);
        targetList.add(target);
    }

    public int size() {
        return targetList.size();
    }

    @Override
    public Iterator<Target> iterator() {
        return targetList.iterator();
    }

    public List<Target> getTargetList() {
        return targetList;
    }

    public Target get(int i) {
        if (i < 0 || i >= this.size()) {
            throw new IndexOutOfBoundsException("i=" + i + " is out of the range");
        }
        return targetList.get(i);
    }

    @Override
    public String toString() { return this.toJson(); }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (baseUrl != null) {
            sb.append("\"baseUrl\":");
            sb.append(baseUrl.toJson());
            sb.append(",");
        }
        sb.append("\"targetList\":[");
        for (int i = 0; i < targetList.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(targetList.get(i).toJson());
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    public String toJson(boolean prettyPrint) {
        if (prettyPrint) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement je = JsonParser.parseString(this.toJson());
            return gson.toJson(je);
        } else {
            return toJson();
        }
    }
}
