package com.kazurayam.inspectus.materialize.selenium;

import com.kazurayam.inspectus.materialize.discovery.Target;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.Metadata;
import com.kazurayam.materialstore.core.Store;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Objects;

public class WebElementMaterializingFunctions extends AbstractMaterializingFunctions {

    Logger logger = LoggerFactory.getLogger(WebElementMaterializingFunctions.class);

    public WebElementMaterializingFunctions(Store store, JobName jobName, JobTimestamp jobTimestamp) {
        super(store, jobName, jobTimestamp);
    }


    public WebElementMaterializingFunction<WebDriver, Target, Map<String, String>, By, Material>
            storeElementScreenshot = (driver, target, attributes, by) -> {
        Objects.requireNonNull(driver);
        Objects.requireNonNull(target);
        Objects.requireNonNull(attributes);
        // take screenshot
        BufferedImage bufferedImage = takeElementScreenshot(driver, by);
        // write the PNG image into the store
        Metadata metadata = Metadata.builder(target.getUrl())
                .putAll(target.getAttributes())
                .putAll(attributes)
                .put("image-width", String.valueOf(bufferedImage.getWidth()))
                .put("image-height", String.valueOf(bufferedImage.getHeight()))
                .build();
        return this.store.write(this.jobName, this.jobTimestamp,
                FileType.PNG,
                metadata, bufferedImage);
    };


    BufferedImage takeElementScreenshot(WebDriver driver, By by) {
        JavascriptExecutor js = (JavascriptExecutor)driver;
        AShot aShot = createAShot(js);
        // take a screenshot of an element in the page
        WebElement webElement = driver.findElement(by);
        Screenshot screenshot = aShot.takeScreenshot(driver, webElement);
        BufferedImage bufferedImage = screenshot.getImage();
        // scroll the view to the top of the page
        js.executeScript("window.scrollTo(0, 0);");
        return bufferedImage;
    }
}
