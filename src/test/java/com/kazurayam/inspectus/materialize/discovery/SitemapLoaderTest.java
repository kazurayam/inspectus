package com.kazurayam.inspectus.materialize.discovery;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.materialize.TestHelper;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SitemapLoaderTest {

    private Logger logger = LoggerFactory.getLogger(SitemapLoaderTest.class);

    private SitemapLoader loader;
    private final Path fixtureDir =
            TestHelper.getFixturesDirectory()
                    .resolve("com/kazurayam/inspectus/materialize/discovery/SitemapLoaderTest");

    @BeforeEach
    public void setup() throws InspectusException {
        assert Files.exists(fixtureDir) :
                String.format("fixtureDir=%s not present",
                        fixtureDir.toAbsolutePath().toString());
        Target baseTopPage = Target.builder("http://myadmin.kazurayam.com").build();
        Target twinTopPage = Target.builder("http://devadmin.kazurayam.com").build();
        loader = new SitemapLoader(baseTopPage, twinTopPage);
        loader.setWithHeaderRecord(true);
    }

    @Test
    public void test_parseJson_Path() throws InspectusException {
        Path json = fixtureDir.resolve("sitemap.json");
        assert Files.exists(json);
        Sitemap sitemap = loader.parseJson(json);
        logger.info("[test_parseJsonPath] " + sitemap.toJson(true));
    }

    @Test
    public void test_parseCSV_withHeader() throws InspectusException {
        Path csv = fixtureDir.resolve("sitemap.csv");
        assert Files.exists(csv);
        Sitemap sitemap = loader.parseCSV(csv);
        logger.info("[test_parseCSV_withHeader] " + sitemap.toJson(true));
        assertEquals(3, sitemap.size());
        assertEquals("http://myadmin.kazurayam.com/proverbs.html",
                sitemap.getBaseTarget(2).getUrl().toString());
        assertEquals("By.cssSelector: #main",
                sitemap.getBaseTarget(2).getHandle().toString());
        assertEquals("03",
                sitemap.getBaseTarget(2).getAttributes().get("step"));
    }

    @Test
    public void test_parseCSV_noHeader() throws InspectusException {
        Path csv = fixtureDir.resolve("sitemap_no_header.csv");
        assert Files.exists(csv);
        loader.setWithHeaderRecord(false);
        Sitemap sitemap = loader.parseCSV(csv);
        logger.info("[test_parseCSV_noHeader] " + sitemap.toJson(true));
        assertEquals(3, sitemap.size());
        assertEquals("http://myadmin.kazurayam.com/proverbs.html",
                sitemap.getBaseTarget(2).getUrl().toString());
        assertEquals("By.cssSelector: #main",
                sitemap.getBaseTarget(2).getHandle().toString());
        assertNull(sitemap.getBaseTarget(2).getAttributes().get("step"));
    }

    @Disabled
    @Test
    public void test_parseCSV_noHeader_setWitHeaderRecordTrue() throws InspectusException {
        Path csv = fixtureDir.resolve("sitemap_no_header.csv");
        assert Files.exists(csv);
        //loader.setWithHeaderRecord(true);  // intentional mistake!
        Sitemap sitemap = loader.parseCSV(csv);
        assertEquals(3, sitemap.size());
    }

    @Test
    public void test_resolveUrl_withProtocol() throws InspectusException {
        SitemapLoader sitemapLoader = new SitemapLoader(Target.builder("http://example.com/pages").build());
        URL url = loader.resolveUrl("http://foo.bar/baz");
        assertEquals("http://foo.bar/baz", url.toString());
    }

    @Test
    public void test_resolveUrl_withSlash() throws InspectusException {
        SitemapLoader sitemapLoader =
                new SitemapLoader(Target.builder("http://example.com/pages").build());
        URL url = sitemapLoader.resolveUrl("/index.html");
        assertEquals("http://example.com/index.html", url.toString());
    }

    @Test
    public void test_resolveURl_withoutSlash() throws InspectusException {
        SitemapLoader sitemapLoader =
                new SitemapLoader(Target.builder("http://example.com/pages/").build());
        URL url = sitemapLoader.resolveUrl("p1.html");
        assertEquals("http://example.com/pages/p1.html", url.toString());
    }

    @Test
    public void test_URL_constructor_withoutSlash() throws MalformedURLException {
        URL baseURL1 = new URL("http://example.com/pages");
        URL derived1 = new URL(baseURL1, "p1.html");
        assertEquals("http://example.com/p1.html", derived1.toExternalForm());
        // the "/" character at the end of baseURL matters
        URL baseURL2 = new URL("http://example.com/pages/");
        URL derived2 = new URL(baseURL2, "p1.html");
        assertEquals("http://example.com/pages/p1.html", derived2.toExternalForm());
    }
}
