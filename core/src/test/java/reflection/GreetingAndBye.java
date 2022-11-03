package reflection;

/**
 * https://www.baeldung.com/java-invoke-static-method-reflection
 */
public class GreetingAndBye {

    public static String greeting(String name) {
        return String.format("Hey %s, nice to meet you!", name);
    }

    public static String goodBye(String name) {
        return String.format("Bye %s, see you next time.", name);
    }
}
