package com.kazurayam.inspectus.support.selenium;

import com.kazurayam.inspectus.core.Inspectus;
import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.TestHelper;
import com.kazurayam.inspectus.fn.FnShootings;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.Material;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.Metadata;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;
import com.kazurayam.materialstore.core.filesystem.Stores;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeleniumShootingsDuckDuckGoTest {

    private Path testClassOutputDir;
    private WebDriver driver;
    private WebDriverFormulas wdf;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setup() throws IOException {
        testClassOutputDir = TestHelper.createTestClassOutputDir(this);
        //
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("headless");
        driver = new ChromeDriver(opt);
        driver.manage().window().setSize(new Dimension(1024, 1000));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        //
        wdf = new WebDriverFormulas();
    }

    @Test
    public void performShootingDuckDuckGo() throws InspectusException {
        // prepare
        Store store = Stores.newInstance(testClassOutputDir.resolve("store"));
        JobName jobName = new JobName("test_DucDucGo");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        SortKeys sortKeys = new SortKeys("step");
        Parameters parameters = Parameters.builder()
                .store(store)
                .jobName(jobName)
                .jobTimestamp(jobTimestamp)
                .sortKeys(sortKeys)
                .build();
        // when
        Inspectus fnShootings = new FnShootings(fn);
        fnShootings.execute(parameters);
        // then
        try {
            Assertions.assertTrue(store.contains(jobName, jobTimestamp));
            assertNotNull(store.selectSingle(jobName, jobTimestamp));
            Assertions.assertTrue(store.selectSingle(jobName, jobTimestamp).getMetadata().containsKey("step"));
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }

    private final Function<Parameters, Intermediates> fn = p -> {
        // pick up the parameter values
        Store store = p .getStore();
        JobName jobName = p.getJobName();
        JobTimestamp jobTimestamp = p.getJobTimestamp();
        // visit the target
        String urlStr = "https://duckduckgo.com";
        URL url = TestHelper.makeURL(urlStr);
        driver.get(urlStr);
        String title = driver.getTitle();
        assertTrue(title.contains("DuckDuckGo"));

        // explicitly wait for <input name="q">
        By inputQ = By.xpath("//input[@name='q']");
        wdf.waitForElementPresent(driver,inputQ, 3);
        // take the 1st screenshot of the blank search page
        Metadata md1 = Metadata.builder(url).put("step", "01").build();
        Material mt1 = MaterializeUtils.takePageScreenshotSaveIntoStore(driver,
                store, jobName, jobTimestamp, md1);
        assertNotNull(mt1);
        assertNotEquals(Material.NULL_OBJECT, mt1);

        // type a keyword "selenium" in the <input> element, then
        // take the 2nd screenshot
        driver.findElement(inputQ).sendKeys("selenium");
        Metadata md2 = Metadata.builder(url).put("step", "02").build();
        Material mt2 = MaterializeUtils.takePageScreenshotSaveIntoStore(driver,
                store, jobName, jobTimestamp, md2);
        assertNotNull(mt2);
        assertNotEquals(Material.NULL_OBJECT, mt2);

        // send ENTER, wait for the search result page to be loaded,
        driver.findElement(inputQ).sendKeys(Keys.RETURN);
        By inputQSelenium = By.xpath("//input[@name='q' and @value='selenium']");
        wdf.waitForElementPresent(driver, inputQSelenium, 3);

        // take the 3rd screenshot
        Metadata md3 = Metadata.builder(url).put("step", "03").build();
        Material mt3 = MaterializeUtils.takePageScreenshotSaveIntoStore(driver,
                store, jobName, jobTimestamp, md3);
        assertNotNull(mt3);
        assertNotEquals(Material.NULL_OBJECT, mt3);

        // done all, exit the Function returning a Intermediate object
        return Intermediates.NULL_OBJECT;

    };
}
