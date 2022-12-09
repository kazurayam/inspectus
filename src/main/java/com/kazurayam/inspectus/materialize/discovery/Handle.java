package com.kazurayam.inspectus.materialize.discovery;

import org.openqa.selenium.By;

import java.util.Objects;

public class Handle {

    private By by;

    public Handle(By by) {
        this.by = by;
    }

    public By getBy() {
        return this.by;
    }

    /**
     * factory method that instantiate a Handle object from its string representation
     *
     * @param handleAsString E.g, "By.cssSelector: #main" or "By.xpath: //*[@id='main']"
     * @return a new instance of Handle
     */
    public static Handle deserialize(String handleAsString) {
        Objects.requireNonNull(handleAsString);
        String[] tokens = handleAsString.trim().split(" ");
        if (tokens[0].startsWith("By.cssSelector")) {
            return new Handle(By.cssSelector(tokens[1]));
        } else if (tokens[0].startsWith("By.xpath")) {
            return new Handle(By.xpath(tokens[1]));
        } else {
            throw new IllegalArgumentException("unknown " + handleAsString);
        }
    }

    @Override
    public String toString() {
        return by.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Handle)) {
            return false;
        }
        Handle other = (Handle)obj;
        return this.by.equals(other.by);
    }

    @Override
    public int hashCode() {
        return this.by.hashCode();
    }
}
