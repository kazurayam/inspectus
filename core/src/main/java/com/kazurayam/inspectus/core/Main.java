package com.kazurayam.inspectus.core;

public class Main {

    public Main() {}

    public String execute(String name) {
        return "Hello, " + name + "!";
    }

    public static void main(String[] main) {
        Main instance = new Main();
        String msg = instance.execute("Inspectus");
    }
}
