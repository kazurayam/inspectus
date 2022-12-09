package com.kazurayam.inspectus.materialize.discovery;

import com.kazurayam.inspectus.materialize.TestHelper;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SitemapLoaderTest {

    private final Path fixtureDir =
            TestHelper.getFixturesDirectory()
                    .resolve("com/kazurayam/inspectus/materialize/discovery/SitemapLoaderTest");

    @BeforeEach
    public void setup() {
        assert Files.exists(fixtureDir) :
                String.format("fixtureDir=%s not present",
                        fixtureDir.toAbsolutePath().toString());
    }

    @Test
    public void test_parseJson_Path() throws MaterialstoreException {
        Path json = fixtureDir.resolve("sitemap.json");
        assert Files.exists(json);
        Target baseTopPage = Target.builder("http://myadmin.kazurayam.com").build();
        Target twinTopPage = Target.builder("http://devadmin.kazurayam.com").build();
        SitemapLoader loader = new SitemapLoader(baseTopPage, twinTopPage);
        Sitemap sitemap = loader.parseJson(json);
        System.out.println(sitemap.toJson(true));
    }

    @Test
    public void test_parseCSV_File() throws MaterialstoreException {
        Path csv = fixtureDir.resolve("sitemap.csv");
        assert Files.exists(csv);
        Target baseTopPage = Target.builder("http://myadmin.kazurayam.com").build();
        Target twinTopPage = Target.builder("http://devadmin.kazurayam.com").build();
        SitemapLoader loader = new SitemapLoader(baseTopPage, twinTopPage);
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
}
