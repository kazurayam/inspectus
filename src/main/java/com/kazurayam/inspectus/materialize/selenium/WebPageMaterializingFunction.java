package com.kazurayam.inspectus.materialize.selenium;

import com.kazurayam.materialstore.core.MaterialstoreException;

@FunctionalInterface
public interface WebPageMaterializingFunction<WebDriver, Target, Map, Material> {

    Material accept(WebDriver driver, Target target, Map attributes) throws MaterialstoreException;

}
