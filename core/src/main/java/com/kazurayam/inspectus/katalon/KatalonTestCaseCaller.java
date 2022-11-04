package com.kazurayam.inspectus.katalon;


import com.kazurayam.inspectus.festum.Festum;
import com.kazurayam.inspectus.festum.InspectusException;

//import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
//import com.kms.katalon.core.keyword.internal.KeywordExecutor
//import com.kms.katalon.core.testcase.TestCase

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * A mimic of "WebUI.callTestCase(TestCase, Map)". It is implemented by
 * com.kms.katalon.core.keyword.builtin.CallTestCaseKeyword#callTestCase()
 * This class "KatalonTestCaseCaller" enables Non-Katalon class to run a Katalon Test Case script
 * just in the same way as Katalon Studio runs a Test Case script.
 * This class "KatalonTestCaseCaller" is independent on `com.kms.katalon.core.*` API at the source code level.
 * This class uses Java Reflection API to link to Katalon runtime.
 *
 */
public class KatalonTestCaseCaller implements Festum {

    public KatalonTestCaseCaller() {}

    @Override
    public Object call(String calleeName, Map<String, Object> binding) throws InspectusException {
        try {
            // verify if the Katalon classes are available in the current classpath
            Class<?> clazz = Class.forName(
                    "com.kms.katalon.core.keyword.internal.KeywordExecutor");
            assert clazz.getSimpleName().equals("KeywordExecutor");
        } catch (Exception e) {
            throw new InspectusException(e,
                    "com.kms.katalon.core.* classes are not available in the current classpath.");
        }

        try {
            // now run the specified Test Case script; possibly for materializing = taking screenshots etc.
            return callKatalonTestCase(calleeName, binding);

        } catch (Exception e) {
            e.printStackTrace();
            throw new InspectusException(e);
        }
    }

    /**
     *
     */
    static Object callKatalonTestCase(String testCaseName, Map<String, Object> binding) throws Exception {
        Object calledTestCase = findTestCase(testCaseName);
        return executeKeywordForPlatform(
                getPLATFORM_BUILT_IN(),
                "callTestCase", calledTestCase, binding);
    }

    static Object findTestCase(String testCaseName) throws InspectusException {
        Objects.requireNonNull(testCaseName);
        try {
            Class<?> clazz = Class.forName("com.kms.katalon.core.testcase.TestCaseFactory");
            Method method = clazz.getMethod("findTestCase", String.class);
            return method.invoke(null, testCaseName);
        } catch (Exception e) {
            throw new InspectusException(e);
        }
    }

    /**
     * This method mimics com.kms.katalon.core.keyword.internal.KeywordExecutor#executeKeywordForPlatform() method, of which
     * method signature is:
     * ```
     * public static Object executeKeywordForPlatform(String platform, String keyword, Object ...params)
     * ```
     */
    static Object executeKeywordForPlatform(
            String platform, String keyword, Object calledTestCase,
            Map<String, Object> binding) throws InspectusException {
        Objects.requireNonNull(platform);
        Objects.requireNonNull(keyword);  // will be "callTestCase" always
        Objects.requireNonNull(calledTestCase);
        Objects.requireNonNull(binding);
        try {
            Class<?> clazz = Class.forName("com.kms.katalon.core.keyword.internal.KeywordExecutor");
            Class<?>[] args = new Class[3];
            args[0] = String.class;
            args[1] = String.class;
            args[2] = Object[].class;
            Method method = clazz.getMethod("executeKeywordForPlatform", args);
            Object[] params = new Object[2];
            params[0] = calledTestCase;
            params[1] = binding;
            return method.invoke(null, platform, keyword, params);
        } catch (Exception e) {
            throw new InspectusException(e);
        }
    }

    static String getPLATFORM_BUILT_IN() {
        return "builtin";
    }
}