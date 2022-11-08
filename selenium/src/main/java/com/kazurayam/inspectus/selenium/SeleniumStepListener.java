package com.kazurayam.inspectus.selenium;

import com.kazurayam.inspectus.core.internal.StepListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumStepListener implements StepListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void stepStarted(String stepName) {
        System.out.println(String.format("%s started", stepName));
    }

    @Override
    public void stepFinished(String stepName) {
        System.out.println(String.format("%s finished", stepName));
    }

}
