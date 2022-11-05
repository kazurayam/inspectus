package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.StepListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KatalonStepListener implements StepListener {

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
