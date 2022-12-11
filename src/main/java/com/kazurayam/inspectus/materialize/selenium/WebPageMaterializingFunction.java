package com.kazurayam.inspectus.materialize.selenium;


import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;

@FunctionalInterface
public interface WebPageMaterializingFunction<WebDriver, Target, Material> {

    Material accept(WebDriver driver, Target target) throws MaterialstoreException;

}
