package com.kazurayam.inspectus.festum;

import java.util.Map;

public interface Festum {

    Object call(String calleeName, Map<String, Object> binding) throws InspectusException;

}
