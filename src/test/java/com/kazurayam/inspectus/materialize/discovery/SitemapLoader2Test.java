package com.kazurayam.inspectus.materialize.discovery;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.materialize.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SitemapLoader2Test {

    private static Logger logger = LoggerFactory.getLogger(SitemapLoader2Test.class);
    private final Path fixtureDir =
            TestHelper.getFixturesDirectory()
                    .resolve("com/kazurayam/inspectus/materialize/discovery/SitemapLoader2Test");

    @BeforeEach
    public void setup() {
        assert Files.exists(fixtureDir) :
                String.format("fixtureDir=%s not present",
                        fixtureDir.toAbsolutePath().toString());
    }

    @Test
    public void test_interpolateString() {
        String text = "Hello, ${name}! Are you OK, ${name}?";
        Map<String, String> bindings = new HashMap<>();
        bindings.put("name", "Alice");
        String result = SitemapLoader2.interpolateString(text, bindings);
        assertTrue(result.contains("Alice"));
        assertEquals("Hello, Alice! Are you OK, Alice?", result);
    }

    @Test
    public void test_loadSitemapCSV_withHeaderRecord() throws InspectusException {
        Path csvFile = fixtureDir.resolve("sitemap.csv");
        String text = SitemapLoader2.readFully(csvFile);
        Map<String, String> bindings = new HashMap<String,String>();
        bindings.put("URL_PREFIX", "http://myadmin.kazurayam.com");
        Sitemap2 sitemap = SitemapLoader2.parseSitemapCSV(text, true, bindings);
        assertNotNull(sitemap);
        logger.info(sitemap.toJson(true));
    }

    @Test
    public void test_loadSitemapCSV_withoutHeaderRecord() throws InspectusException {
        Path csvFile = fixtureDir.resolve("sitemap_no_header.csv");
        String text = SitemapLoader2.readFully(csvFile);
        Map<String, String> bindings = new HashMap<String,String>();
        bindings.put("URL_PREFIX", "http://myadmin.kazurayam.com");
        Sitemap2 sitemap = SitemapLoader2.parseSitemapCSV(text, false, bindings);
        assertNotNull(sitemap);
        logger.info(sitemap.toJson(true));
    }


    @Test
    public void test_loadSitemapJson() throws InspectusException {
        // when
        Path jsonFile = fixtureDir.resolve("sitemap.json");
        Sitemap2 sitemap = SitemapLoader2.loadSitemapJson(jsonFile);
        // then
        logger.info(sitemap.toJson(true));
        assertTrue(sitemap.get(0).getAttributes().containsKey("description"));
        assertEquals(sitemap.get(0).getAttributes().get("description"), "original");
    }

    @Test
    public void test_loadSitemapJson_parameterized() throws InspectusException {
        // when
        Path jsonFile = fixtureDir.resolve("sitemap_parameterized.json");
        Map<String, String> bindings = new HashMap<>();
        bindings.put("URL_PREFIX", "https://kazurayam.github.com/myApple/");
        Sitemap2 sitemap = SitemapLoader2.loadSitemapJson(jsonFile, bindings);
        // then
        logger.info(sitemap.toJson(true));
        assertEquals("https://kazurayam.github.com/myApple/page1.html",
                sitemap.get(0).getUrl().toString());
        assertTrue(sitemap.get(0).getAttributes().containsKey("description"));
        assertEquals(sitemap.get(0).getAttributes().get("description"), "original");
    }

}
