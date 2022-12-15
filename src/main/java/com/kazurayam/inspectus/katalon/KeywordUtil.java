package com.kazurayam.inspectus.katalon;

import com.kazurayam.inspectus.core.InspectusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A proxy that internally calls `WebUI.comment(String message)` keyword.
 * This class calls the Katalon class using Java Reflection API runtime.
 * This class does not link the Katalon class statically compile time.
 */
public class KeywordUtil {

    private Logger logger = LoggerFactory.getLogger(KeywordUtil.class);
    private KeywordUtil() {}

    public static void logInfo(String message) throws InspectusException {
        // check if the Katalon classes are available in the runtime classpath
        KeywordExecutor.validateKatalonClasspath();
        Object result =
                KeywordExecutor.executeKeywordForPlatform(
                        ITestCaseCaller.getPLATFORM_BUILT_IN(),
                        "comment", message);
    }
}
