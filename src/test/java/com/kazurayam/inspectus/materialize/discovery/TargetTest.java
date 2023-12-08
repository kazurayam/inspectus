package com.kazurayam.inspectus.materialize.discovery;

import com.kazurayam.inspectus.core.InspectusException;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TargetTest {

    Logger logger = LoggerFactory.getLogger(TargetTest.class);

    @Test
    public void test_default_By() throws InspectusException, MalformedURLException {
        Target target =
                new Target.Builder("http://example.com").build();
        assertEquals(new URL("http://example.com"), target.getUrl());
        assertEquals("By.xpath: /html/body", target.getHandle().toString());
        logger.info(target.toJson(true));
    }

    @Test
    public void test_GoogleSearchPage() throws MalformedURLException, InspectusException {
        Target target =
                new Target.Builder("https://www.google.com")
                        .handle(new Handle(By.cssSelector("input[name=\"q\"]")))
                        .build();
        assertEquals(new URL("https://www.google.com"), target.getUrl());
        assertEquals("By.cssSelector: input[name=\"q\"]", target.getHandle().toString());
    }

    @Test
    public void test_copyWithBy() throws InspectusException {
        Target target =
                new Target.Builder("https://www.google.com")
                        .build();
        Target t = target.copyWith(new Handle(By.xpath("/html/body/section")));
        assertEquals("By.xpath: /html/body/section", t.getHandle().toString());
    }

    @Test
    public void test_copyWithAttribute() throws InspectusException {
        Target target =
                new Target.Builder("https://www.google.com")
                        .handle(new Handle(By.cssSelector("input[name=\"q\"]")))
                        .build();
        Target newTarget = target.copyWith("environment", "Development");
        assertEquals("Development", newTarget.get("environment"));
    }

    @Test
    public void test_copyWithAttributes() throws InspectusException {
        Target target =
                new Target.Builder("https://www.google.com")
                        .handle(new Handle(By.cssSelector("input[name=\"q\"]")))
                        .build();
        Map<String, String> attributes = Collections.singletonMap("environment", "Development");
        Target newTarget = target.copyWith(attributes);
        assertEquals("Development", newTarget.get("environment"));
    }

    @Test
    public void test_copyConstructor() throws InspectusException {
        Target target =
                new Target.Builder("https://www.google.com")
                        .handle(new Handle(By.cssSelector("input[name=\"q\"]")))
                        .put("foo", "bar")
                        .build();
        assertEquals("bar", target.get("foo"));
        Target copied = new Target(target);
        assertEquals("bar", copied.get("foo"));
    }

    @Test
    public void test_builder_URL() throws MalformedURLException {
        URL url = new URL("http://www.example.com");
        Target target = Target.builder(url).build();
        assertNotNull(target);
    }

    @Test
    public void test_builder_String() throws InspectusException {
        String urlString = "http://www.example.com";
        Target target = Target.builder(urlString).build();
        assertNotNull(target);
    }

    @Test
    public void test_deserialize() throws InspectusException {
        String json = "{\"url\":\"http://myadmin.kazurayam.com/index.html\",\"handle\":\"By.cssSelector: #main\",\"attributes\":{\"step\":\"01\"}}";
        Target t = Target.deserialize(json);
        assertEquals("http://myadmin.kazurayam.com/index.html", t.getUrl().toString());
        assertEquals("By.cssSelector: #main", t.getHandle().toString());
        assertEquals("01", t.getAttributes().get("step"));
    }

    @Test
    public void test_getHandle_getBy() throws InspectusException {
        Target target =
                new Target.Builder("https://www.google.com")
                        .handle(new Handle(By.cssSelector("input[name=\"q\"]")))
                        .build();
        Handle handle = target.getHandle();
        By by = handle.getBy();
        assertTrue(by instanceof By.ByCssSelector);
        logger.info("[test_getHandle_getBy] " + by);
    }
}
