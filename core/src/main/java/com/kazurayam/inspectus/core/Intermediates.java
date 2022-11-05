package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.filesystem.MaterialList;
import com.kazurayam.materialstore.core.filesystem.SortKeys;

import java.util.LinkedHashMap;
import java.util.Map;

public class Intermediates {

    private final MaterialList materialList;
    private final MaterialProductGroup materialProductGroup;
    private final SortKeys sortKeys;
    private final Double criteria;

    private static final String KEY_materialList = "materialList";
    private static final String KEY_materialProductGroup = "materialProductGroup";
    private static final String KEY_sortKeys = "sortKeys";
    private static final String KEY_criteria = "criteria";

    private Intermediates(Builder b) {
        this.materialList = b.materialList;
        this.materialProductGroup = b.materialProductGroup;
        this.sortKeys = b.sortKeys;
        this.criteria = b.criteria;
    }

    public MaterialList getMaterialList() {
        return materialList;
    }
    public MaterialProductGroup getMaterialProductGroup() { return materialProductGroup; }
    public SortKeys getSortKeys() { return sortKeys; }
    public Double getCriteria() {
        return criteria;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(KEY_materialList, materialList);
        m.put(KEY_materialProductGroup, materialProductGroup);
        m.put(KEY_sortKeys, sortKeys);
        m.put(KEY_criteria, criteria);
        return m;
    }
    public static class Builder {
        private MaterialList materialList;
        private MaterialProductGroup materialProductGroup;
        private SortKeys sortKeys;
        private Double criteria;
        public Builder() {
            materialList = MaterialList.NULL_OBJECT;
            materialProductGroup = MaterialProductGroup.NULL_OBJECT;
            sortKeys = SortKeys.NULL_OBJECT;
            criteria = 0.0;
        }
        public Builder(Map<String, Object> m) {
            this();
            if (m.get(KEY_materialList) != null &&
                    m.get(KEY_materialList) instanceof MaterialList) {
                materialList = (MaterialList)m.get(KEY_materialList);
            }
            if (m.get(KEY_materialProductGroup) != null &&
                    m.get(KEY_materialProductGroup) instanceof MaterialProductGroup) {
                materialProductGroup = (MaterialProductGroup)m.get(KEY_materialProductGroup);
            }
            if (m.get(KEY_sortKeys) != null &&
                    m.get(KEY_sortKeys) instanceof SortKeys) {
                sortKeys = (SortKeys)m.get(KEY_sortKeys);
            }
            if (m.get(KEY_criteria) != null &&
                    m.get(KEY_criteria) instanceof Double) {
                criteria = (Double)m.get(KEY_criteria);
            }
        }

        public Builder materialList(MaterialList materialList) {
            this.materialList = materialList;
            return this;
        }
        public Builder materialProductGroup(MaterialProductGroup mpg) {
            this.materialProductGroup = mpg;
            return this;
        }
        public Builder sortKeys(SortKeys sortKeys) {
            this.sortKeys = sortKeys;
            return this;
        }
        public Builder criteria(Double criteria) {
            this.criteria = criteria;
            return this;
        }
        public Intermediates build() {
            return new Intermediates(this);
        }
    }


}
