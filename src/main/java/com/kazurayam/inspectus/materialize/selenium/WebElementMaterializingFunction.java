package com.kazurayam.inspectus.materialize.selenium;

import com.kazurayam.materialstore.core.MaterialstoreException;

@FunctionalInterface
public interface WebElementMaterializingFunction<WebDriver, Target, Map, By, Material> {

    Material accept(WebDriver driver, Target target, Map attributes, By by) throws MaterialstoreException;
}
