package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Parameters {

    private final Path baseDir;
    private final Store store;
    private final Store backup;
    private final JobName jobName;
    private final JobTimestamp jobTimestamp;
    private final SortKeys sortKeys;
    private final Double criteria;

    public static final String KEY_baseDir = "baseDir";
    public static final String KEY_store = "store";
    public static final String KEY_backup = "backup";
    public static final String KEY_jobName = "jobName";
    public static final String KEY_jobTimestamp = "jobTimestamp";
    public static final String KEY_materializeScriptName = "materializeScriptName";
    public static final String KEY_sortKeys = "sortKeys";
    public static final String KEY_criteria = "criteria";

    private Parameters(Builder b) {
        this.baseDir = b.baseDir;
        this.store = b.store;
        this.backup = b.backup;
        this.jobName = b.jobName;
        this.jobTimestamp = b.jobTimestamp;
        this.sortKeys = b.sortKeys;
        this.criteria = b.criteria;
    }
    public Boolean containsBaseDir() { return baseDir != null; }
    public Boolean containsCriteria() { return criteria >= 0.0; }
    public Boolean containsStore() { return store != Store.NULL_OBJECT; }
    public Boolean containsBackup() { return backup != Store.NULL_OBJECT;}
    public Boolean containsJobName() { return jobName != JobName.NULL_OBJECT; }
    public Boolean containsJobTimestamp() { return jobTimestamp != JobTimestamp.NULL_OBJECT; }

    public Path getBaseDir() { return baseDir; }
    public Store getStore() { return store; }
    public Store getBackup() { return backup; }
    public JobName getJobName() { return jobName; }
    public JobTimestamp getJobTimestamp() { return jobTimestamp; }
    public SortKeys getSortKeys() { return sortKeys; }
    public Double getCriteria() {
        return criteria;
    }


    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(KEY_baseDir, baseDir);
        m.put(KEY_store, store);
        m.put(KEY_backup, backup);
        m.put(KEY_jobName, jobName);
        m.put(KEY_jobTimestamp, jobTimestamp);
        m.put(KEY_sortKeys, sortKeys);
        m.put(KEY_criteria, criteria);
        return m;
    }

    public static class Builder {
        private Path baseDir;
        private Store store;
        private Store backup;
        private JobName jobName;
        private JobTimestamp jobTimestamp;
        private SortKeys sortKeys;
        private Double criteria;

        public Builder() {
            this.baseDir = null;
            this.store = Store.NULL_OBJECT;
            this.backup = Store.NULL_OBJECT;
            this.jobName = JobName.NULL_OBJECT;
            this.jobTimestamp = JobTimestamp.NULL_OBJECT;
            this.sortKeys = SortKeys.NULL_OBJECT;
            this.criteria = 0.0;
        }
        public Builder baseDir(Path baseDir) {
            this.baseDir = baseDir;
            return this;
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
        public Builder sortKeys(SortKeys sortKeys) {
            this.sortKeys = sortKeys;
            return this;
        }
        public Builder criteria(Double criteria) throws InspectusException {
            if (criteria < 0.0) {
                throw new InspectusException("criteria must be >= 0.0");
            }
            this.criteria = criteria;
            return this;
        }
        public Parameters build() {
            return new Parameters(this);
        }
    }
}
