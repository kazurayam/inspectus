package com.kazurayam.inspectus.materialize.selenium;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.materialize.TestHelper;
import com.kazurayam.inspectus.materialize.discovery.Handle;
import com.kazurayam.inspectus.materialize.discovery.Target;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.QueryOnMetadata;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.core.Stores;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebPageMaterializingFunctionsTest {

    private static Store store;
    private WebDriver driver;

    @BeforeAll
    public static void beforeAll() {
        Path testCaseOutputDir =
                TestHelper.createTestClassOutputDir(WebPageMaterializingFunctionsTest.class);
        Path root = testCaseOutputDir.resolve("store");
        store = Stores.newInstance(root);
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("headless");
        driver = new ChromeDriver(opt);
        driver.manage().window().setSize(new Dimension(1024, 768));
    }

    @Test
    void test_storeHTMLSource() throws InspectusException, MaterialstoreException {
        Target target = new Target.Builder("https://www.google.com")
                .handle(new Handle(By.cssSelector("input[name=\"q\"]")))
                .put("description", "search page")
                .build();
        JobName jobName = new JobName("test_storeHTMLSource");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        // open the page in browser
        driver.navigate().to(target.getUrl());
        // get HTML source of the page, save it into the store
        Map<String, String> attribute = Collections.singletonMap("step", "01");
        WebPageMaterializingFunctions pmf = new WebPageMaterializingFunctions(store, jobName, jobTimestamp);
        Material createdMaterial = pmf.storeHTMLSource.accept(driver, target, attribute);
        assertNotNull(createdMaterial);
        // assert that a material has been created
        Material selectedMaterial = store.selectSingle(jobName, jobTimestamp, FileType.HTML, QueryOnMetadata.ANY);
        assertTrue(Files.exists(selectedMaterial.toPath()));
        assertEquals(createdMaterial, selectedMaterial);
    }

    @Test
    void test_storeEntirePageScreenshot() throws InspectusException, MaterialstoreException {
        Target target = new Target.Builder("https://github.com/kazurayam")
                .handle(new Handle(By.cssSelector("div.application-main main")))
                .put("description", "GitHub/kazurayam")
                .build();
        JobName jobName = new JobName("test_storeEntirePageScreenshot");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        // open the page in browser
        driver.navigate().to(target.getUrl());
        // take an entire page screenshot, write the image into the store
        WebPageMaterializingFunctions pmf = new WebPageMaterializingFunctions(store, jobName, jobTimestamp);
        Material createdMaterial = pmf.storeEntirePageScreenshot.accept(driver, target, Collections.emptyMap());
        assertNotNull(createdMaterial);
        // assert that a material has been created
        Material selectedMaterial = store.selectSingle(jobName, jobTimestamp, FileType.PNG, QueryOnMetadata.ANY);
        assertTrue(Files.exists(selectedMaterial.toPath()));
        assertEquals(createdMaterial, selectedMaterial);
    }


    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
