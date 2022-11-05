package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
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
public interface ITestCaseCaller {

    static final Logger logger = LoggerFactory.getLogger(ITestCaseCaller.class);

    public static String KEY_result = "result";

    default Intermediates callTestCase(String calleeName, Parameters parameters) throws InspectusException {
        // check if the Katalon classes are available
        KeywordExecutor.validateKatalonClasspath();

        // now run the specified Test Case script; possibly for materializing = taking screenshots etc.
        Object result;
        try {
            Object calledTestCase = findTestCase(calleeName);
            Object[] params = new Object[2];
            params[0] = calledTestCase;
            params[1] = parameters.toMap();
            result = KeywordExecutor.executeKeywordForPlatform(getPLATFORM_BUILT_IN(),
                    "callTestCase", params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InspectusException(e);
        }

        // convert an Object instance returned from the Katalon Test Case
        // into an instance of Intermediates
        if (result instanceof Map<?, ?>) {
            Map<String, Object> m = new LinkedHashMap<String, Object>();
            Map<?, ?> casted = (Map<?, ?>)result;
            for (Object k : casted.keySet()) {
                if (k instanceof String) {
                    m.put((String)k, casted.get(k));
                } else {
                    logger.error(String.format("in the intermediates returned by a Test Case" +
                            ", found a key '%s' which is not a String", k.toString()));
                }
            }
            return new Intermediates.Builder(m).build();
        } else {
            throw new InspectusException(String.format(
                    "Test Case '%s' must return an instance of Map but actually returned %s",
                    calleeName, result.getClass().getSimpleName()));
        }
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

    static String getPLATFORM_BUILT_IN() {
        return "builtin";
    }
}