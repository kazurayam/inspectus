package com.kazurayam.inspectus.core;

import com.kazurayam.inspectus.core.internal.StepListener;

public class StdStepListener implements StepListener {

    @Override
    public void stepStarted(String stepName) {
        System.out.println(String.format("%s started", stepName));
    }

    @Override
    public void info(String message) {
        System.out.println(message);
    }
    @Override
    public void stepFinished(String stepName) {
        System.out.println(String.format("%s finished", stepName));
    }

}
