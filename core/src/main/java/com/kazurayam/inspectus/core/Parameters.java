package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.Store;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Parameters {

    private final Store store;
    private final Store backup;
    private final JobName jobName;
    private final JobTimestamp jobTimestamp;
    private final String materializeScriptName;
    private Parameters(Builder b) {
        this.store = b.store;
        this.backup = b.backup;
        this.jobName = b.jobName;
        this.jobTimestamp = b.jobTimestamp;
        this.materializeScriptName = b.materializeScriptName;
    }

    public Store getStore() { return store; }
    public Store getBackup() { return backup; }
    public JobName getJobName() { return jobName; }
    public JobTimestamp getJobTimestamp() { return jobTimestamp; }
    public String getMaterializeScriptName() { return materializeScriptName; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("store", store);
        m.put("backup", backup);
        m.put("jobName", jobName);
        m.put("jobTimestamp", jobTimestamp);
        m.put("materializeScriptName", materializeScriptName);
        return m;
    }

    public static class Builder {
        private Store store;
        private Store backup;
        private JobName jobName;
        private JobTimestamp jobTimestamp;
        private String materializeScriptName;

        public Builder() {
            this.store = Store.NULL_OBJECT;
            this.backup = Store.NULL_OBJECT;
            this.jobName = JobName.NULL_OBJECT;
            this.jobTimestamp = JobTimestamp.NULL_OBJECT;
            this.materializeScriptName = "";
        }
        public Builder store(Store store) {
            this.store = store;
            return this;
        }
        public Builder backup(Store backup) {
            this.backup = backup;
            return this;
        }
        public Builder jobName(JobName jobName) {
            this.jobName = jobName;
            return this;
        }
        public Builder jobTimestamp(JobTimestamp jobTimestamp) {
            this.jobTimestamp = jobTimestamp;
            return this;
        }
        public Builder materializeScriptName(String materializeScriptName) {
            this.materializeScriptName = materializeScriptName;
            return this;
        }
        public Parameters build() {
            return new Parameters(this);
        }
    }
}
