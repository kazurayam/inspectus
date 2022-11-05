package com.kazurayam.inspectus.plain;

import com.kazurayam.inspectus.core.InspectusException;
import com.kazurayam.inspectus.core.Intermediates;
import com.kazurayam.inspectus.core.Parameters;
import com.kazurayam.inspectus.core.Shootings;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public final class PlainShootings extends Shootings {

    @Override
    public Intermediates step2_materialize(Parameters parameters)
        throws InspectusException {
        Path projectDir = Paths.get(System.getProperty("user.dir"));
        Path fixturesDir = projectDir.resolve("src/test/fixtures");
        throw new RuntimeException("TODO");
    }
}
