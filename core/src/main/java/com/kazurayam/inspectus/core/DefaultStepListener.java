package com.kazurayam.inspectus.core;

public class DefaultStepListener implements StepListener {

    @Override
    public void stepStarted(String stepName) {
        System.out.println(String.format("%s started", stepName));
    }

    @Override
    public void stepFinished(String stepName) {
        System.out.println(String.format("%s finished", stepName));
    }

}
