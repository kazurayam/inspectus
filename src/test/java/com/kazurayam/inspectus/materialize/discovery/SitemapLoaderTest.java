package com.kazurayam.inspectus.materialize.discovery;

import com.kazurayam.inspectus.materialize.TestHelper;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SitemapLoaderTest {

    private SitemapLoader loader;
    private final Path fixtureDir =
            TestHelper.getFixturesDirectory()
                    .resolve("com/kazurayam/inspectus/materialize/discovery/SitemapLoaderTest");

    @BeforeEach
    public void setup() throws MaterialstoreException {
        assert Files.exists(fixtureDir) :
                String.format("fixtureDir=%s not present",
                        fixtureDir.toAbsolutePath().toString());
        Target baseTopPage = Target.builder("http://myadmin.kazurayam.com").build();
        Target twinTopPage = Target.builder("http://devadmin.kazurayam.com").build();
        loader = new SitemapLoader(baseTopPage, twinTopPage);
    }

    @Test
    public void test_parseJson_Path() throws MaterialstoreException {
        Path json = fixtureDir.resolve("sitemap.json");
        assert Files.exists(json);
        Sitemap sitemap = loader.parseJson(json);
        System.out.println(sitemap.toJson(true));
    }

    @Test
    public void test_parseCSV_File() throws MaterialstoreException {
        Path csv = fixtureDir.resolve("sitemap.csv");
        assert Files.exists(csv);
        Sitemap sitemap = loader.parseCSV(csv);
        System.out.println(sitemap.toJson(true));
        assertEquals(3, sitemap.size());
        assertEquals("http://myadmin.kazurayam.com/proverbs.html",
                sitemap.getBaseTarget(2).getUrl().toString());
        assertEquals("By.cssSelector: #main",
                sitemap.getBaseTarget(2).getHandle().toString());
        assertEquals("03",
                sitemap.getBaseTarget(2).getAttributes().get("step"));
    }

    @Test
    public void test_resolveUrl_withProtocol() throws MaterialstoreException {
        SitemapLoader sitemapLoader = new SitemapLoader(Target.builder("http://example.com/pages").build());
        URL url = loader.resolveUrl("http://foo.bar/baz");
        assertEquals("http://foo.bar/baz", url.toString());
    }

    @Test
    public void test_resolveUrl_withSlash() throws MaterialstoreException {
        SitemapLoader sitemapLoader =
                new SitemapLoader(Target.builder("http://example.com/pages").build());
        URL url = sitemapLoader.resolveUrl("/index.html");
        assertEquals("http://example.com/index.html", url.toString());
    }

    @Test
    public void test_resolveURl_withoutSlash() throws MaterialstoreException {
        SitemapLoader sitemapLoader =
                new SitemapLoader(Target.builder("http://example.com/pages").build());
        URL url = sitemapLoader.resolveUrl("p1.html");
        assertEquals("http://example.com/p1.html", url.toString());
    }
}
