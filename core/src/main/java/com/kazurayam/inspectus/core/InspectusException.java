package com.kazurayam.inspectus.core;

public final class InspectusException extends Exception {

    public InspectusException(Throwable t) {
        super(t);
    }

    public InspectusException(String message) {
        super(message);
    }

    public InspectusException(Throwable t, String message) {
        super(message, t);
    }
}

