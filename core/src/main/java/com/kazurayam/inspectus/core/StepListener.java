package com.kazurayam.inspectus.core;

public interface StepListener {

    public void stepStarted(String stepName);

    public void stepFinished(String stepName);

}
