package com.kazurayam.inspectus.materialize.selenium;

import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Store;
import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import java.util.Objects;

abstract class AbstractMaterializingFunctions {

    Logger logger = LoggerFactory.getLogger(AbstractMaterializingFunctions.class);

    protected Store store;
    protected JobName jobName;
    protected JobTimestamp jobTimestamp;
    protected int scrollTimeout = 500;  // in milliseconds

    protected AbstractMaterializingFunctions(Store store, JobName jobName, JobTimestamp jobTimestamp) {
        Objects.requireNonNull(store);
        Objects.requireNonNull(jobName);
        Objects.requireNonNull(jobTimestamp);
        this.store = store;
        this.jobName = jobName;
        this.jobTimestamp = jobTimestamp;
    }


    public void setScrollTimeout(int scrollTimeout) {
        if (scrollTimeout < 0 || 8000 < scrollTimeout) {
            throw new IllegalArgumentException("scrollTimeout(" + scrollTimeout
                    + ") must be in the range of [0, 8000]");
        }
        this.scrollTimeout = scrollTimeout;
    }


    AShot createAShot(JavascriptExecutor js) {
        // look up the device-pixel-ratio of the current machine
        float dpr = resolveDevicePixelRatio(js);
        AShot aShot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .shootingStrategy(ShootingStrategies.viewportPasting(
                        ShootingStrategies.scaling(dpr),
                        this.scrollTimeout));
        return aShot;
    }


    float resolveDevicePixelRatio(JavascriptExecutor js) {
        // look up the device-pixel-ratio of the current machine
        Object jsValue = js.executeScript("return window.devicePixelRatio;");
        float dprFloat = 1.0f;
        if (jsValue == null) {
            logger.warn("jsValue was null. will use 1.0f.");
            dprFloat = 1.0f;
        } else if (jsValue instanceof Double) {
            dprFloat = ((Double)jsValue).floatValue();
        } else if (jsValue instanceof Long) {
            dprFloat = (Long)jsValue;
        } else {
            logger.warn("jsValue was unknown type: "
                    + jsValue.getClass().getName() + ". will use 1.0f");
        }
        return dprFloat;
    }

}
