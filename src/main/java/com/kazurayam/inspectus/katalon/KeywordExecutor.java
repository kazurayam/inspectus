package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.InspectusException;

import java.lang.reflect.Method;
import java.util.Objects;

public class KeywordExecutor {

    /*
     * This method invokes the executeKeywordForPlatform method of
     * the com.kms.katalon.core.keyword.internal.KeywordExecutor class
     * using Java Reflection API.
     */
    public static Object executeKeywordForPlatform(String platform, String keyword, Object... params)
            throws InspectusException {
        Objects.requireNonNull(platform);
        Objects.requireNonNull(keyword);  // will be "callTestCase" always
        Objects.requireNonNull(params);
        //
        validateKatalonClasspath();
        //
        try {
            Class<?> clazz = Class.forName("com.kms.katalon.core.keyword.internal.KeywordExecutor");
            Class<?>[] args = new Class[3];
            args[0] = String.class;
            args[1] = String.class;
            args[2] = Object[].class;
            Method method = clazz.getMethod("executeKeywordForPlatform", args);
            return method.invoke(null, platform, keyword, params);
        } catch (Exception e) {
            throw new InspectusException(e);
        }
    }

    public static void validateKatalonClasspath() throws InspectusException {
        try {
            // verify if the Katalon classes are available in the current classpath
            Class<?> clazz = Class.forName(
                    "com.kms.katalon.core.keyword.internal.KeywordExecutor");
            assert clazz.getSimpleName().equals("KeywordExecutor");
        } catch (Exception e) {
            throw new InspectusException(
                    "com.kms.katalon.core.* classes are not available in the current classpath.", e);
        }
    }
}
