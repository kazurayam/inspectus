package com.kazurayam.inspectus.materialize.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebDriverFormulasTest {

    WebDriver driver;
    WebDriverManager manager;
    WebDriverFormulas formulas;

    @BeforeAll
    public static void beforeClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setup() {
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("headless");
        driver = new ChromeDriver(opt);
        formulas = new WebDriverFormulas();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void test_createWebDriverWait() {
        WebDriverWait wait = formulas.createWebDriverWait(driver, 3);
        assertNotNull(wait);
        assertTrue(wait instanceof WebDriverWait);
    }

    @Test
    public void test_navigateTo() throws MalformedURLException {
        URL duckduckgo = new URL("https://duckduckgo.com/");
        By search_form_input_homepage =
                By.cssSelector("#search_form_input_homepage");
        formulas.navigateTo(driver, duckduckgo,
                search_form_input_homepage, 3);
        assertEquals(duckduckgo.toString(), driver.getCurrentUrl());
        // type "selenium" as query
        driver.findElement(search_form_input_homepage)
                .sendKeys("selenium");
        // click the button to navigate to the result page
        By search_button_homepage =
                By.cssSelector("#search_button_homepage");
        By search_form =
                By.cssSelector("#search_form");
        formulas.navigateByClick(driver, search_button_homepage,
                search_form, 30);
        assertTrue(driver.getCurrentUrl()
                .startsWith("https://duckduckgo.com/?q=selenium"));
    }
}
