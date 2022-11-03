package reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Invoke a Static Method Using Java Reflection API, Baeldung
 * https://www.baeldung.com/java-invoke-static-method-reflection
 */
public class GreetingAndByTest {

    @Test
    public void invokePublicMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        Class<GreetingAndBye> clazz = GreetingAndBye.class;
        Method method = clazz.getMethod("greeting", String.class);
        // Action
        // the 1st arg to invoke must be null when we invoke a static method
        Object result = method.invoke(null, "Eric");
        // Assert
        assertEquals("Hey Eric, nice to meet you", result);
    }
}
