package com.kazurayam.inspectus.core;

import java.util.Objects;
public class Environment implements Comparable<Environment> {

    public static final Environment NULL_OBJECT = new Environment("");

    private final String value;

    public Environment(String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof Environment)) {
            return false;
        }
        Environment other = (Environment)obj;
        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int compareTo(Environment other) {
        return this.getValue().compareTo(other.getValue());
    }
}
