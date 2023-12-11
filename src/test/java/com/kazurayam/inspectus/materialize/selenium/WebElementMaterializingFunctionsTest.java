package com.kazurayam.inspectus.materialize.selenium;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.materialize.discovery.Handle;
import com.kazurayam.inspectus.materialize.discovery.Target;
import com.kazurayam.inspectus.zest.TestOutputOrganizerFactory;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.QueryOnMetadata;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.core.Stores;
import com.kazurayam.unittest.TestOutputOrganizer;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebElementMaterializingFunctionsTest {

    private static final TestOutputOrganizer too =
            TestOutputOrganizerFactory.create(WebElementMaterializingFunctionsTest.class);
    private static Store store;
    private WebDriver driver;

    @BeforeAll
    public static void beforeAll() throws IOException {
        too.cleanClassOutputDirectory();
        Path root = too.getClassOutputDirectory().resolve("store");
        store = Stores.newInstance(root);
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("headless");
        opt.addArguments("--remote-allow-origins=*");  // https://stackoverflow.com/questions/75718422/org-openqa-selenium-remote-http-connectionfailedexception-unable-to-establish-w
        driver = new ChromeDriver(opt);
        driver.manage().window().setSize(new Dimension(1024, 768));
    }

    @Test
    void test_storeElementScreenshot() throws InspectusException, MaterialstoreException {
        By by = By.cssSelector("div.Layout-sidebar");
        Target target = new Target.Builder("https://github.com/kazurayam")
                .handle(new Handle(by))
                .put("description", "GitHub/kazurayam")
                .build();
        JobName jobName = new JobName("test_storeElementScreenshot");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        // open the page in browser
        driver.navigate().to(target.getUrl());
        // wait for the element to be present

        // take a screenshot of an HTML element, write the image into the store
        WebElementMaterializingFunctions emf =
                new WebElementMaterializingFunctions(store, jobName, jobTimestamp);
        emf.setScrollTimeout(1000);
        Material createdMaterial =
                emf.storeElementScreenshot.accept(driver, target, Collections.emptyMap(), by);
        assertNotNull(createdMaterial);
        // assert that a material has been created
        Material selectedMaterial = store.selectSingle(jobName, jobTimestamp, FileType.PNG, QueryOnMetadata.ANY);
        assertTrue(Files.exists(selectedMaterial.toPath()));
        assertEquals(createdMaterial, selectedMaterial);
        assertTrue(selectedMaterial.getMetadata().containsKey("image-width"), "image-width is missing");
        assertTrue(Integer.valueOf(selectedMaterial.getMetadata().get("image-width")) > 0);
        assertTrue(selectedMaterial.getMetadata().containsKey("image-height"), "image-height is missing");
        assertTrue(Integer.valueOf(selectedMaterial.getMetadata().get("image-height")) > 0);
    }

    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
