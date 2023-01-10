package com.kazurayam.inspectus.materialize.url;


import com.kazurayam.materialstore.core.MaterialstoreException;

public interface URLMaterializingFunction<Target, Material> {

    Material accept(Target target) throws MaterialstoreException;
}
