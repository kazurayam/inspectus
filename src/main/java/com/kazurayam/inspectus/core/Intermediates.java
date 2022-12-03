package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialList;

import java.util.LinkedHashMap;
import java.util.Map;

public class Intermediates {

    public static final Intermediates NULL_OBJECT = new Builder().build();

    private final MaterialList materialList;
    private final MaterialProductGroup materialProductGroup;
    private final JobTimestamp jobTimestampLeft;
    private final JobTimestamp jobTimestampRight;
    private final Environment environmentLeft;
    private final Environment environmentRight;
    public static final String KEY_materialList = "materialList";
    public static final String KEY_materialProductGroup = "materialProductGroup";
    public static final String KEY_jobTimestampLeft = "jobTimestampLeft";
    public static final String KEY_jobTimestampRight = "jobTimestampRight";
    public static final String KEY_environmentLeft = "environmentLeft";
    public static final String KEY_environmentRight = "environmentRight";

    public static Builder builder() {
        return new Builder();
    }
    public static Builder builder(Intermediates source) { return new Builder(source); }

    private Intermediates(Builder b) {
        this.materialList = b.materialList;
        this.materialProductGroup = b.materialProductGroup;
        this.jobTimestampLeft = b.jobTimestampLeft;
        this.jobTimestampRight = b.jobTimestampRight;
        this.environmentLeft = b.environmentLeft;
        this.environmentRight = b.environmentRight;
    }
    public Boolean containsMaterialList() {
        return this.materialList != MaterialList.NULL_OBJECT;
    }
    public Boolean containsMaterialProductGroup() {
        return this.materialProductGroup != MaterialProductGroup.NULL_OBJECT;
    }
    public Boolean containsJobTimestampLeft() {
        return this.jobTimestampLeft != JobTimestamp.NULL_OBJECT;
    }
    public Boolean containsJobTimestampRight() {
        return this.jobTimestampRight != JobTimestamp.NULL_OBJECT;
    }
    public Boolean containsEnvironmentLeft() { return this.environmentLeft != Environment.NULL_OBJECT; }
    public Boolean containsEnvironmentRight() { return this.environmentRight != Environment.NULL_OBJECT; }

    public MaterialList getMaterialList() {
        return materialList;
    }
    public MaterialProductGroup getMaterialProductGroup() { return materialProductGroup; }
    public JobTimestamp getJobTimestampLeft() { return jobTimestampLeft; }
    public JobTimestamp getJobTimestampRight() { return jobTimestampRight; }
    public Environment getEnvironmentLeft() { return environmentLeft; }
    public Environment getEnvironmentRight() { return environmentRight; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(KEY_materialList, materialList);
        m.put(KEY_materialProductGroup, materialProductGroup);
        m.put(KEY_jobTimestampLeft, jobTimestampLeft);
        m.put(KEY_jobTimestampRight, jobTimestampRight);
        m.put(KEY_environmentLeft, environmentLeft);
        m.put(KEY_environmentRight, environmentRight);
        return m;
    }
    public static class Builder {
        private MaterialList materialList;
        private MaterialProductGroup materialProductGroup;
        private JobTimestamp jobTimestampLeft;
        private JobTimestamp jobTimestampRight;
        private Environment environmentLeft;
        private Environment environmentRight;

        public Builder() {
            materialList = MaterialList.NULL_OBJECT;
            materialProductGroup = MaterialProductGroup.NULL_OBJECT;
            jobTimestampLeft = JobTimestamp.NULL_OBJECT;
            jobTimestampRight = JobTimestamp.NULL_OBJECT;
            environmentLeft = Environment.NULL_OBJECT;
            environmentRight = Environment.NULL_OBJECT;
        }
        public Builder(Intermediates source) {
            materialList = source.materialList;
            materialProductGroup = source.materialProductGroup;
            jobTimestampLeft = source.jobTimestampLeft;
            jobTimestampRight = source.jobTimestampRight;
            environmentLeft = source.environmentLeft;
            environmentRight = source.environmentRight;
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
            if (m.get(KEY_jobTimestampLeft) != null &&
                    m.get(KEY_jobTimestampLeft) instanceof JobTimestamp) {
                jobTimestampLeft = (JobTimestamp)m.get(KEY_jobTimestampLeft);
            }
            if (m.get(KEY_jobTimestampRight) != null &&
                    m.get(KEY_jobTimestampRight) instanceof JobTimestamp) {
                jobTimestampRight = (JobTimestamp)m.get(KEY_jobTimestampRight);
            }
            if (m.get(KEY_environmentLeft) != null &&
                    m.get(KEY_environmentLeft) instanceof Environment) {
                environmentLeft = (Environment)m.get(KEY_environmentLeft);
            }
            if (m.get(KEY_environmentRight) != null &&
                    m.get(KEY_environmentRight) instanceof Environment) {
                environmentRight = (Environment)m.get(KEY_environmentRight);
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
        public Builder jobTimestampLeft(JobTimestamp jt) {
            this.jobTimestampLeft = jt;
            return this;
        }
        public Builder jobTimestampRight(JobTimestamp jt) {
            this.jobTimestampRight = jt;
            return this;
        }
        public Builder environmentLeft(Environment profile) {
            this.environmentLeft = profile;
            return this;
        }

        public Builder environmentRight(Environment profile) {
            this.environmentRight = profile;
            return this;
        }

        public Intermediates build() {
            return new Intermediates(this);
        }
    }


}
