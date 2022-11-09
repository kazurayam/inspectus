package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialList;

import java.util.LinkedHashMap;
import java.util.Map;

public class Intermediates {

    public static final Intermediates NULL_OBJECT = new Intermediates.Builder().build();

    private final MaterialList materialList;
    private final MaterialProductGroup materialProductGroup;
    private final String profileLeft;
    private final String profileRight;
    private final JobTimestamp jobTimestampLeft;
    private final JobTimestamp jobTimestampRight;
    public static final String KEY_materialList = "materialList";
    public static final String KEY_materialProductGroup = "materialProductGroup";
    public static final String KEY_profileLeft = "profileLeft";
    public static final String KEY_profileRight = "profileRight";
    public static final String KEY_jobTimestampLeft = "jobTimestampLeft";
    public static final String KEY_jobTimestampRight = "jobTimestampRight";
    private Intermediates(Builder b) {
        this.materialList = b.materialList;
        this.materialProductGroup = b.materialProductGroup;
        this.profileLeft = b.profileLeft;
        this.profileRight = b.profileRight;
        this.jobTimestampLeft = b.jobTimestampLeft;
        this.jobTimestampRight = b.jobTimestampRight;
    }
    public Boolean containsMaterialList() {
        return this.materialList != MaterialList.NULL_OBJECT;
    }
    public Boolean containsMaterialProductGroup() {
        return this.materialProductGroup != MaterialProductGroup.NULL_OBJECT;
    }
    public Boolean containsProfileLeft() {
        return ! this.profileLeft.equals("");
    }
    public Boolean containsProfileRight() {
        return ! this.profileRight.equals("");
    }
    public Boolean containsJobTimestampLeft() {
        return this.jobTimestampLeft != JobTimestamp.NULL_OBJECT;
    }
    public Boolean containsJobTimestampRight() {
        return this.jobTimestampRight != JobTimestamp.NULL_OBJECT;
    }

    public MaterialList getMaterialList() {
        return materialList;
    }
    public MaterialProductGroup getMaterialProductGroup() { return materialProductGroup; }
    public String getProfileLeft() { return profileLeft; }
    public String getProfileRight() { return profileRight; }
    public JobTimestamp getJobTimestampLeft() { return jobTimestampLeft; }
    public JobTimestamp getJobTimestampRight() { return jobTimestampRight; }
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(KEY_materialList, materialList);
        m.put(KEY_materialProductGroup, materialProductGroup);
        m.put(KEY_profileLeft, profileLeft);
        m.put(KEY_profileRight, profileRight);
        m.put(KEY_jobTimestampLeft, jobTimestampLeft);
        m.put(KEY_jobTimestampRight, jobTimestampRight);
        return m;
    }
    public static class Builder {
        private MaterialList materialList;
        private MaterialProductGroup materialProductGroup;
        private String profileLeft;
        private String profileRight;
        private JobTimestamp jobTimestampLeft;
        private JobTimestamp jobTimestampRight;

        public Builder() {
            materialList = MaterialList.NULL_OBJECT;
            materialProductGroup = MaterialProductGroup.NULL_OBJECT;
            profileLeft = "";
            profileRight = "";
            jobTimestampLeft = JobTimestamp.NULL_OBJECT;
            jobTimestampRight = JobTimestamp.NULL_OBJECT;
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
            if (m.get(KEY_profileLeft) != null &&
                    m.get(KEY_profileLeft) instanceof String) {
                profileLeft = (String)m.get(KEY_profileLeft);
            }
            if (m.get(KEY_profileRight) != null &&
                    m.get(KEY_profileRight) instanceof String) {
                profileRight = (String)m.get(KEY_profileRight);
            }
            if (m.get(KEY_jobTimestampLeft) != null &&
                    m.get(KEY_jobTimestampLeft) instanceof JobTimestamp) {
                jobTimestampLeft = (JobTimestamp)m.get(KEY_jobTimestampLeft);
            }
            if (m.get(KEY_jobTimestampRight) != null &&
                    m.get(KEY_jobTimestampRight) instanceof JobTimestamp) {
                jobTimestampRight = (JobTimestamp)m.get(KEY_jobTimestampRight);
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
        public Builder profileLeft(String pf) {
            this.profileLeft = pf;
            return this;
        }
        public Builder profileRight(String pf) {
            this.profileRight = pf;
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

        public Intermediates build() {
            return new Intermediates(this);
        }
    }


}
