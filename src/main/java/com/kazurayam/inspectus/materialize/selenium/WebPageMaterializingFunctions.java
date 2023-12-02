package com.kazurayam.inspectus.materialize.selenium;

import com.kazurayam.inspectus.materialize.discovery.Target;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.Metadata;
import com.kazurayam.materialstore.core.Store;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Objects;

public class WebPageMaterializingFunctions extends AbstractMaterializingFunctions {

    Logger logger = LoggerFactory.getLogger(WebPageMaterializingFunctions.class);

    public WebPageMaterializingFunctions(Store store, JobName jobName, JobTimestamp jobTimestamp) {
        super(store, jobName, jobTimestamp);
    }


    /**
     * get HTML source of the target web page, pretty-print it, save it into
     * the store
     */
    public WebPageMaterializingFunction<WebDriver, Target, Map<String,String>, Material>
            storeHTMLSource = (driver, target, attributes) -> {
        Objects.requireNonNull(driver);
        Objects.requireNonNull(target);
        Objects.requireNonNull(attributes);
        //-------------------------------------------------------------
        // get the HTML source from browser
        String rawHtmlSource = driver.getPageSource();
        // pretty print HTML source
        Document doc = Jsoup.parse(rawHtmlSource, "", Parser.htmlParser());
        doc.outputSettings().indentAmount(2);
        String ppHtml = doc.toString();
        //-------------------------------------------------------------
        // write the HTML source into the store
        Metadata metadata = Metadata.builder(target.getUrl())
                .putAll(target.getAttributes())
                .putAll(attributes)
                .build();
        return this.store.write(this.jobName, this.jobTimestamp,
                FileType.HTML, metadata, ppHtml);
    };

    /**
     *
     */
    public WebPageMaterializingFunction<WebDriver, Target, Map<String, String>, Material>
            storeEntirePageScreenshot = (driver, target, attributes) -> {
        Objects.requireNonNull(driver);
        Objects.requireNonNull(target);
        Objects.requireNonNull(attributes);
        // take screenshot
        BufferedImage bufferedImage = takeFullPageScreenshot(driver);
        // write the PNG image into the store
        Metadata metadata = Metadata.builder(target.getUrl())
                .putAll(target.getAttributes())
                .putAll(attributes)
                .build();
        return this.store.write(this.jobName, this.jobTimestamp,
                FileType.PNG,
                metadata, bufferedImage);
    };

    public WebPageMaterializingFunction<WebDriver, Target, Map<String, String>, Material>
            storeEntirePageScreenshotAsJpeg = (driver, target, attributes) -> {
        Objects.requireNonNull(driver);
        Objects.requireNonNull(target);
        Objects.requireNonNull(attributes);
        // take screenshot
        BufferedImage bufferedImage = takeFullPageScreenshot(driver);
        // write the PNG image into the store
        Metadata metadata = Metadata.builder(target.getUrl())
                .putAll(target.getAttributes())
                .putAll(attributes)
                .build();
        return this.store.write(this.jobName, this.jobTimestamp,
                FileType.JPEG,
                metadata, bufferedImage);
    };


    BufferedImage takeFullPageScreenshot(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor)driver;
        AShot aShot = createAShot(js);
        // take a screenshot of entire view of the page
        Screenshot screenshot = aShot.takeScreenshot(driver);
        BufferedImage bufferedImage = screenshot.getImage();
        // scroll the view to the top of the page
        js.executeScript("window.scrollTo(0, 0);");
        return bufferedImage;
    }

}
