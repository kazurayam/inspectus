package com.kazurayam.inspectus.materialize.discovery;

import com.kazurayam.inspectus.core.InspectusException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SitemapTest {
    private static final Logger logger =
            LoggerFactory.getLogger(SitemapTest.class);
    private static Target topPage;
    private static Target page1;
    private static Target page2;

    @BeforeAll
    public static void beforeAll() throws InspectusException {
        String baseURL = "https://kazurayam.github.com/myApple/";
        topPage = Target.builder(baseURL)
                .handle(new Handle(By.xpath("//h1/a[@href='" + baseURL + "']"))).build();
        page1 = Target.builder(baseURL + "page1.html")
                .handle(new Handle(By.xpath("//img[@id='apple']"))).build();
        page2 = Target.builder(baseURL + "page2.html")
                .handle(new Handle(By.xpath("//img[@id='apple']"))).build();
    }

    @Test
    public void test_toJson_prettyPrint() {
        Sitemap sm = new Sitemap();
        sm.setBaseUrl(topPage);
        sm.add(page1);
        sm.add(page2);
        assertEquals(2, sm.size());
        assertTrue(sm.getBaseUrl().equals(topPage));
        assertTrue(sm.get(0).equals(page1));
        assertTrue(sm.get(1).equals(page2));
        logger.info(sm.toJson(true));
    }

    @Test
    public void test_getTargetList() {
        // when
        Sitemap sm = new Sitemap();
        sm.setBaseUrl(topPage);
        sm.add(page1);
        sm.add(page2);
        // then
        assertEquals(2, sm.getTargetList().size());
    }

}
