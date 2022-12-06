package com.kazurayam.inspectus.core;

public class UncheckedInspectusException extends RuntimeException {

    public UncheckedInspectusException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedInspectusException(String message) {
        super(message);
    }

    public UncheckedInspectusException(Throwable cause) {
        super(cause);
    }
}
