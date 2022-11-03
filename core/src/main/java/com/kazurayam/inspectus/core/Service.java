package com.kazurayam.inspectus.core;

import java.util.Collections;
import java.util.Map;

public final class Service {

    private Festum festum = null;

    Service() {
        festum = null;
    }

    public void setFestum(Festum festum) {
        this.festum = festum;
    }

    public void execute(Map<String, Object> parameters) throws InspectusException {
        if (festum == null) {
            throw new InspectusException("festum must not be null");
        }

        // ante festum
        AnteFestum ante = new AnteMaterialize();
        ante.preprocess(Collections.emptyMap());

        // in festum
        festum.call("Test Cases/materialize", Collections.emptyMap());

        // post festum
        PostFestum post = new PostMaterialize();
        post.postprocess(Collections.emptyMap());
    }
}
