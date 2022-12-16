package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.UncheckedInspectusException;
import com.kazurayam.inspectus.core.internal.StepListener;

public class KatalonStepListener implements StepListener {

    @Override
    public void stepStarted(String stepName) {
        try {
            KeywordUtil.logInfo(String.format("%s started", stepName));
        } catch (Exception e) {
            throw new UncheckedInspectusException(e);
        }
    }

    @Override
    public void info(String message) {
        try {
            KeywordUtil.logInfo(message);
        } catch (Exception e) {
            throw new UncheckedInspectusException(e);
        }
    }
    @Override
    public void stepFinished(String stepName) {
        try {
            KeywordUtil.logInfo(String.format("%s finished", stepName));
        } catch (Exception e) {
            throw new UncheckedInspectusException(e);
        }
    }
}
