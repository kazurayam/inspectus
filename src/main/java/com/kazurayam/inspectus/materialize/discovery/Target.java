package com.kazurayam.inspectus.materialize.discovery;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.materialstore.core.filesystem.Jsonifiable;
import com.kazurayam.materialstore.core.util.JsonUtil;
import org.openqa.selenium.By;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A Target object is immutable.
 */
public final class Target implements Jsonifiable {

    public static final Target NULL_OBJECT;

    static {
        try {
            NULL_OBJECT = new Builder("https://example.com/").build();
        } catch (InspectusException e) {
            throw new RuntimeException(e);
        }
    }

    private final URL url;
    private final Handle handle;
    private final ImmutableMap<String, String> attributes;

    public static Builder builder(URL url) {
        return new Builder(url);
    }

    public static Builder builder(String urlString) throws InspectusException {
        return new Builder(urlString);
    }

    /**
     * primary constructor is private, takes the Builder instance
     */
    private Target(Builder builder) {
        this.url = builder.url;
        this.handle = builder.handle;
        this.attributes =
                ImmutableMap.<String, String>builder()
                        .putAll(builder.attributes)
                        .build();
    }

    /*
     * copy constructor
     */
    public Target(Target source) {
        this.url = source.getUrl();
        this.handle = source.getHandle();
        this.attributes = source.getAttributes();
    }

    /*
     * creates a new instance of Target class while replacing the By with specified value
     */
    public Target copyWith(Handle handle) {
        return Target.builder(this.getUrl())
                .handle(handle)
                .putAll(this.getAttributes())
                .build();
    }

    /*
     * creates a new instance of Target class while adding
     * a new attribute with kew=value.
     */
    public Target copyWith(String key, String value) {
        return Target.builder(this.getUrl())
                .handle(this.getHandle())
                .putAll(this.getAttributes())
                .put(key, value)
                .build();
    }

    /*
     * creates a new instance of Target class while adding attributes specified
     */
    public Target copyWith(Map<String, String> newAttributes) {
        return Target.builder(this.getUrl())
                .handle(this.getHandle())
                .putAll(this.getAttributes())
                .putAll(newAttributes)
                .build();
    }

    public URL getUrl() {
        return this.url;
    }
    public Handle getHandle() { return this.handle; }
    public ImmutableMap<String, String> getAttributes() { return this.attributes; }
    public Object get(String key) { return this.attributes.get(key); }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"url\":\"");
        sb.append(JsonUtil.escapeAsJsonString(url.toExternalForm()));
        sb.append("\",");
        sb.append("\"handle\":\"");
        sb.append(JsonUtil.escapeAsJsonString(handle.toString()));
        sb.append("\",");
        sb.append("\"attributes\":");
        sb.append("{");
        StringBuilder sbAttr = new StringBuilder();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            if (sbAttr.length() > 0) {
                sbAttr.append(",");
            }
            sbAttr.append("\"");
            sbAttr.append(JsonUtil.escapeAsJsonString(entry.getKey()));
            sbAttr.append("\"");
            sbAttr.append(":");
            sbAttr.append("\"");
            sbAttr.append(JsonUtil.escapeAsJsonString(entry.getValue()));
            sbAttr.append("\"");
        }
        sb.append(sbAttr);
        sb.append("}");
        sb.append("}");
        return sb.toString();
    }
    public String toJson(boolean prettyPrint) {
        if (prettyPrint) {
            return JsonUtil.prettyPrint(toJson(), Map.class);
        } else {
            return toJson();
        }
    }

    @Override
    public String toString() {
        return this.toJson();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Target)) {
            return false;
        }
        Target other = (Target)obj;
        return this.getUrl().equals(other.getUrl()) &&
                this.getHandle().equals(other.getHandle());
    }

    @Override
    public int hashCode() {
        return this.getUrl().hashCode();
    }


    /**
     * Read a String in JSON format, instantiate a Target object.
     *
     * @param jsonText for example,
     * <PRE>{
     *       "url": "http://myadmin.kazurayam.com/index.html",
     *       "handle": "By.cssSelector: #main",
     *       "attributes": {
     *                 "step": "01"
     *                 }
     *     }
     * </PRE>
     * or
     * <PRE>{
     *       "url": "http://myadmin.kazurayam.com",
     *       "handle": "By.xpath: //*[@id\u003d\u0027main\u0027]",
     *       "attributes": {}
     *     }
     * </PRE>
     * @return an instance of Target class constructed from a String in JSON format
     * @throws InspectusException when failed deserializing this
     */
    public static Target deserialize(String jsonText)
            throws InspectusException {
        Objects.requireNonNull(jsonText);
        JsonElement jsonElement = new JsonParser().parse(jsonText);
        JsonObject jo = jsonElement.getAsJsonObject();
        return deserialize(jo);
    }

    public static Target deserialize(JsonObject jo) throws InspectusException {
        // deserialize a URL
        String urlValue = jo.get("url").getAsString();
        URL url = null;
        try {
            url = new URL(urlValue);
        } catch (MalformedURLException e) {
            throw new InspectusException(e);
        }
        // deserialize a Target
        String handleValue = jo.get("handle").getAsString();
        Handle handle = Handle.deserialize(handleValue);
        // deserialize a Map
        JsonObject attributes = jo.getAsJsonObject("attributes");
        Map<String, String> attrs = new HashMap<>();
        for (String k : attributes.keySet()) {
            attrs.put(k, attributes.get(k).getAsString());
        }
        //
        return Target.builder(url).handle(handle).putAll(attrs).build();
    }

    public static class Builder {
        private final URL url;
        private Handle handle = new Handle(By.xpath("/html/body"));
        private Map<String, String> attributes = new LinkedHashMap<>();
        public Builder(String urlString) throws InspectusException {
            try {
                this.url = new URL(urlString);
            } catch (MalformedURLException e) {
                throw new InspectusException(e);
            }
        }
        public Builder(URL url) {
            this.url = url;
        }
        public Builder(Target source) {
            this.url = source.getUrl();
            this.handle = source.getHandle();
        }
        public Builder handle(Handle handle) {
            this.handle = handle;
            return this;
        }
        public Builder put(String key, String value) {
            this.attributes.put(key, value);
            return this;
        }
        public Builder putAll(Map<String, String> attrs) {
            this.attributes.putAll(attrs);
            return this;
        }
        public Target build() {
            return new Target(this);
        }
    }
}
