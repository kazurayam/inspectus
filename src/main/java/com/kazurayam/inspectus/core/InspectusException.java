package com.kazurayam.inspectus.core;

public class InspectusException extends Exception {

    public InspectusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InspectusException(String message) {
        super(message);
    }

    public InspectusException(Throwable cause) {
        super(cause);
    }
}
