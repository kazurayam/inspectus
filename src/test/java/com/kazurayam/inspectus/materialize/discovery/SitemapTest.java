package com.kazurayam.inspectus.materialize.discovery;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SitemapTest {

    private static final Logger logger =
            LoggerFactory.getLogger(SitemapTest.class);

    private static Target baseTopPage;
    private static Target twinTopPage;
    private static Target baseIndex;
    private static Target baseRepositories;
    private static Target baseProverbs;

    @BeforeAll
    public static void beforeAll() throws InspectusException {
        baseTopPage = Target.builder("http://myadmin.kazurayam.com")
                .handle(new Handle(By.xpath("//*[@id='main']"))).build();
        twinTopPage = Target.builder("http://devadmin.kazurayam.com")
                .handle(new Handle(By.xpath("//*[@id='main']"))).build();
        baseIndex = Target.builder("http://myadmin.kazurayam.com/index.html")
                .handle(new Handle(By.cssSelector("#main"))).put("step", "01").build();
        baseRepositories = Target.builder("http://myadmin.kazurayam.com/repositories.html")
                .handle(new Handle(By.cssSelector("#main"))).put("step", "02").build();
        baseProverbs = Target.builder("http://myadmin.kazurayam.com/proverbs.html")
                .handle(new Handle(By.cssSelector("#main"))).put("step", "03").build();
    }

    @Test
    public void test_constructorSingle(){
        Sitemap sm = new Sitemap(baseTopPage);
        assertEquals(0, sm.size());
        assertEquals(baseTopPage, sm.getBaseTopPage());
        assertEquals(Target.NULL_OBJECT, sm.getTwinTopPage());
    }

    @Test
    public void test_constructorTwin() {
        Sitemap sm = new Sitemap(baseTopPage, twinTopPage);
        assertEquals(0, sm.size());
        assertEquals(baseTopPage, sm.getBaseTopPage());
        assertEquals(twinTopPage, sm.getTwinTopPage());
    }

    @Test
    public void test_add_target() {
        Sitemap sm = new Sitemap(baseTopPage, twinTopPage);
        sm.add(baseIndex);
        sm.add(baseRepositories);
        sm.add(baseProverbs);
        assertEquals(3, sm.size());
    }

    @Test
    public void test_add_target_with_spec() {
        Sitemap sm = new Sitemap(baseTopPage, twinTopPage);
        sm.add("/index.html", new Handle(By.cssSelector("#main")));
        sm.add("/repositories.html", new Handle(By.xpath("//*[@id='main']")));
        sm.add("proverbs.html", new Handle(By.cssSelector("#main")));
        List<Target> baseTargetList = sm.getBaseTargetList();
        for (int i = 0; i < baseTargetList.size(); i++) {
            logger.info(String.format("[test_add_target_asFile] %d %s",
                    i, sm.getBaseTarget(i).toJson()) );
        }
        assertEquals("http://myadmin.kazurayam.com/index.html",
                baseTargetList.get(0).getUrl().toString());
        assertEquals("http://myadmin.kazurayam.com/repositories.html",
                baseTargetList.get(1).getUrl().toString());
        assertEquals("http://myadmin.kazurayam.com/proverbs.html",
                baseTargetList.get(2).getUrl().toString());
    }

    @Test
    public void test_deriveTwinTarget() {
        Sitemap sm = new Sitemap(baseTopPage, twinTopPage);
        sm.add(baseIndex);
        assertEquals("devadmin.kazurayam.com",
                sm.deriveTwinTarget(baseIndex).getUrl().getHost());
    }

    @Test
    public void test_getTwinTarget() {
        Sitemap sm = new Sitemap(baseTopPage, twinTopPage);
        sm.add(baseIndex);
        assertEquals("devadmin.kazurayam.com",
                sm.getTwinTarget(0).getUrl().getHost());
    }

    @Test
    public void test_getBaseTargetList() {
        Sitemap sm = new Sitemap(baseTopPage, twinTopPage);
        sm.add(baseIndex);
        sm.add(baseRepositories);
        sm.add(baseProverbs);
        List<Target> list = sm.getBaseTargetList();
        assertEquals(3, list.size());
        for (Target t : list) {
            logger.info("[test_getBaseTargetList] " + t.toJson());
        }
    }

    @Test
    public void test_getTwinTargetList() {
        Sitemap sm = new Sitemap(baseTopPage, twinTopPage);
        sm.add(baseIndex);
        sm.add(baseRepositories);
        sm.add(baseProverbs);
        List<Target> list = sm.getTwinTargetList();
        assertEquals(3, list.size());
        for (Target t : list) {
            logger.info("[test_getTwinTargetList] " + t.toJson());
        }
    }

    @Test
    public void test_toJson_prettyPrint() {
        Sitemap sm = new Sitemap(baseTopPage, twinTopPage);
        sm.add(baseIndex);
        sm.add(baseRepositories);
        sm.add(baseProverbs);
        String ppJson = sm.toJson(true);
        logger.info("[test_toJson_prettyPrint] " + ppJson);
    }
}
