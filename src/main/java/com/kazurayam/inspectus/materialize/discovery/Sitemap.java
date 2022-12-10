package com.kazurayam.inspectus.materialize.discovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Sitemap {

    private final Logger logger = LoggerFactory.getLogger(Sitemap.class);

    private final Target baseTopPage;
    private final Target twinTopPage;
    private final List<Target> targetList;

    public Sitemap(Target baseTopPage) {
        this(baseTopPage, Target.NULL_OBJECT);
    }

    public Sitemap(Target baseTopPage, Target twinTopPage) {
        this.baseTopPage = baseTopPage;
        this.twinTopPage = twinTopPage;
        targetList = new ArrayList<>();
    }

    public Target getBaseTopPage() {
        return baseTopPage;
    }

    public Target getTwinTopPage() {
        return twinTopPage;
    }

    public void add(Target target) {
        Objects.requireNonNull(target);
        if (target.getUrl().getHost().equals(baseTopPage.getUrl().getHost())) {
            targetList.add(target);
        } else {
            logger.warn(String.format("host names differ. target=%s, baseTopPage=%s.",
                    target.toJson(), baseTopPage.toJson()));
        }
    }

    public void add(String spec, Handle handle) throws IllegalArgumentException {
        Objects.requireNonNull(spec);
        Objects.requireNonNull(handle);
        try {
            URL url = new URL(baseTopPage.getUrl(), spec);
            Target target = new Target.Builder(url).handle(handle).build();
            this.targetList.add(target);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public int size() {
        return targetList.size();
    }

    public Target getBaseTarget(int index) {
        return targetList.get(index);
    }

    public List<Target> getBaseTargetList() {
        return Collections.unmodifiableList(targetList);
    }

    public Target getTwinTarget(int index) {
        if (this.twinTopPage != Target.NULL_OBJECT) {
            return deriveTwinTarget(targetList.get(index));
        } else {
            logger.warn("getTwinTarget() was called but twinTopPage is not set");
            return Target.NULL_OBJECT;
        }
    }

    public List<Target> getTwinTargetList() {
        List<Target> result = new ArrayList<>();
        for (Target t : targetList) {
            result.add(new Target.Builder(deriveTwinTarget(t)).build());
        }
        return Collections.unmodifiableList(result);
    }

    public Target deriveTwinTarget(Target base) {
        String baseProtocol = base.getUrl().getProtocol();
        //String baseHost = base.getUrl().getHost();
        String twinHost = this.twinTopPage.getUrl().getHost();
        int basePort = base.getUrl().getPort();
        String baseFile = base.getUrl().getFile();
        try {
            URL twinUrl = new URL(baseProtocol, twinHost, basePort, baseFile);
            Target twinTarget = new Target.Builder(twinUrl).handle(base.getHandle()).build();
            return twinTarget;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return this.toJson();
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"baseTopPage\":");
        sb.append(baseTopPage.toJson());
        sb.append(",");
        sb.append("\"twinTopPage\":");
        sb.append(twinTopPage.toJson());
        sb.append(",");
        sb.append("\"targetList\":[");
        for (int i = 0; i < targetList.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            Target t = targetList.get(i);
            sb.append(t.toJson());
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    public String toJson(boolean prettyPrint) {
        if (prettyPrint) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(this.toJson());
            return gson.toJson(je);
        } else {
            return toJson();
        }
    }

}
