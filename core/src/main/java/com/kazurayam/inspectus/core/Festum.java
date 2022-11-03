package com.kazurayam.inspectus.core;

import java.util.Map;

public interface Festum {

    Object call(String calleeName, Map<String, Object> binding) throws InspectusException;

}
