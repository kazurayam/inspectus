package com.kazurayam.inspectus.materialize.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;

/**
 * A collection of WebDriver code snippet that are frequently repeated in UI test cases:
 *
 * 1. wait for an HTML element to be present in a HTML in browser
 *
 * 2. navigate to a URL and wait for the page is completely loaded
 *
 * 3. send click on an HTML element expecting the browser to navigate to a new page, and wait for the new page is comoletely loaded
 */
public final class WebDriverFormulas {

    Logger logger = LoggerFactory.getLogger(WebDriverFormulas.class);

    public WebDriverFormulas() {}

    /**
     * Will assume that we have a web browser is opened with some URL loaded.
     * Will assume that there is an HTML element to be clicked in the current page.
     * When you invoke this method, it will click the clickThis element.
     * Will assume that the browser will navigate to a new URL by the click.
     * This method will wait for the handleInTheNext element to become present
     * in the new URL.
     * This will wait until the timeout in secods to expire.
     *
     * @param driver          WebDriver instance
     * @param clickThis       the HTML element in the current web page to click
     * @param handleInTheNext will wait for the handle to become in the new page
     * @param timeout         e.g, 10 seconds
     * @return If found returns the WebElement pointed by the handleInTheNext By.
     */
    public final WebElement navigateByClick(WebDriver driver,
                                            By clickThis,
                                            By handleInTheNext,
                                            long timeout)
            throws TimeoutException {
        this.waitForElementPresent(driver, clickThis, timeout);
        driver.findElement(clickThis).click();
        return this.waitForElementPresent(driver, handleInTheNext, timeout);
    }

    /**
     * will let the browser navigate to the URL specified,
     * wait for the handle element to become present in the new page.
     *
     * @param driver  WebDriver instance
     * @param url     the target URL to let browser navigate to
     * @param handle  will wait for the URL is completely loaded and the handle element become present in there
     * @param timeout e.g, 10 seconds
     * @return the WebElement that the handle points to
     */
    public final WebElement navigateTo(WebDriver driver,
                                       URL url,
                                       By handle,
                                       long timeout)
            throws TimeoutException {
        driver.get(url.toExternalForm());
        return this.waitForElementPresent(driver, handle, timeout);
    }

    /**
     * This will wait for the handle become present in the browser window
     * opened by the driver.
     * will fail if the handle is unpresent after the timeout seconds.
     *
     * @param driver  WebDriver instance
     * @param handle  By instance
     * @param timeout e.g, 10 seconds
     * @return the WebElement that the handle points to
     */
    public final WebElement waitForElementPresent(WebDriver driver,
                                                  By handle,
                                                  long timeout)
            throws TimeoutException {
        WebDriverWait wait = createWebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions.presenceOfElementLocated(handle));
    }

    /**
     * Create an instance of org.openqa.selenium.WebDriverWait.
     * Please note that Selenium3 and Selenium4 has a difference in
     * the signature of constructor of WebDriverWait.
     * They are incompatible.
     * This createWebDriverWait method absorbs this incompatibility
     * and silently returns an instance of WebDriverWait
     * given with either of Selenium3 or Selenium4 in the CLASSPATH.
     *
     * @param driver WebDriver
     * @param timeout seconds
     * @return an instance of WebDriverWait
     */
    public final WebDriverWait createWebDriverWait(WebDriver driver, long timeout) {
        return this.createWebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public final WebDriverWait createWebDriverWait(WebDriver driver, Duration duration) {
        Objects.requireNonNull(driver);
        Objects.requireNonNull(duration);
        //return new WebDriverWait(driver, timeout);
        Class<?> clazz;
        String fullyQualifiedClassName = "org.openqa.selenium.support.ui.WebDriverWait";
        try {
            clazz = Class.forName(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        Constructor<?> selenium4constructor;
        try {
            selenium4constructor = clazz.getConstructor(WebDriver.class, Duration.class);
            return (WebDriverWait)selenium4constructor.newInstance(driver, duration);
        } catch (NoSuchMethodException e) {
            logger.debug(String.format("%s(WebDriver, Duration) of Selenium4 is NOT found in the CLASSPATH",
                    fullyQualifiedClassName));
            logger.debug(String.format("will try to load %s(WebDriver, long) of Selenium3)",
                    fullyQualifiedClassName));
            //
            Constructor<?> selenium3constructor;
            try {
                selenium3constructor = clazz.getConstructor(WebDriver.class, long.class);
                long timeout = duration.getSeconds();
                return (WebDriverWait)selenium3constructor.newInstance(driver, timeout);
            } catch (NoSuchMethodException x) {
                throw new IllegalStateException(
                        String.format("%s(WebDriver, long) of Selenium3 is not found in the CLASSPATH", fullyQualifiedClassName),
                        x);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
