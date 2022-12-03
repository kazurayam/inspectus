package com.kazurayam.inspectus.core.internal;

public interface StepListener {

    public void stepStarted(String stepName);

    public void stepFinished(String stepName);

}
