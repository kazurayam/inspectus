package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;
import com.kazurayam.materialstore.core.filesystem.metadata.IgnoreMetadataKeys;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Parameters {

    public static final Parameters NULL_OBJECT = new Parameters.Builder().build();
    private final Path baseDir;
    private final Store store;
    private final Store backup;
    private final JobName jobName;
    private final JobTimestamp jobTimestamp;
    private final String profileLeft;
    private final String profileRight;
    private final SortKeys sortKeys;
    private final Double threshold;
    private final IgnoreMetadataKeys ignoreMetadataKeys;
    private final JobTimestamp cleanOlderThan;

    public static final String KEY_baseDir = "baseDir";
    public static final String KEY_store = "store";
    public static final String KEY_backup = "backup";
    public static final String KEY_cleanOlderThan = "cleanOlderThan";
    public static final String KEY_jobName = "jobName";
    public static final String KEY_jobTimestamp = "jobTimestamp";
    public static final String KEY_profileLeft = "profileLeft";
    public static final String KEY_profileRight = "profileRight";
    public static final String KEY_sortKeys = "sortKeys";
    public static final String KEY_threshold = "threshold";
    public static final String KEY_ignoreMetadataKeys = "ignoreMetadataKeys";

    public static Builder builder() {
        return new Builder();
    }

    private Parameters(Builder b) {
        this.baseDir = b.baseDir;
        this.store = b.store;
        this.backup = b.backup;
        this.jobName = b.jobName;
        this.jobTimestamp = b.jobTimestamp;
        this.profileLeft = b.profileLeft;
        this.profileRight = b.profileRight;
        this.sortKeys = b.sortKeys;
        this.threshold = b.threshold;
        this.ignoreMetadataKeys = b.ignoreMetadataKeys;
        this.cleanOlderThan = b.cleanOlderThan;
    }
    public Boolean containsBackup() { return backup != Store.NULL_OBJECT;}
    public Boolean containsBaseDir() { return baseDir != null; }
    public Boolean containsCleanOlderThan() { return cleanOlderThan != JobTimestamp.NULL_OBJECT; }
    public Boolean containsIgnoreMetadataKeys() { return ignoreMetadataKeys != IgnoreMetadataKeys.NULL_OBJECT; }
    public Boolean containsJobName() { return jobName != JobName.NULL_OBJECT; }
    public Boolean containsJobTimestamp() { return jobTimestamp != JobTimestamp.NULL_OBJECT; }
    public Boolean containsProfileLeft() { return ! this.profileLeft.equals(""); }
    public Boolean containsProfileRight() {
        return ! this.profileRight.equals("");
    }
    public Boolean containsSortKeys() { return sortKeys != null; }
    public Boolean containsStore() { return store != Store.NULL_OBJECT; }
    public Boolean containsThreshold() { return threshold >= 0.0; }

    public Store getBackup() { return backup; }
    public Path getBaseDir() { return baseDir; }
    public JobTimestamp getCleanOlderThan() { return cleanOlderThan; }
    public IgnoreMetadataKeys getIgnoreMetadataKeys() { return ignoreMetadataKeys; }
    public JobName getJobName() { return jobName; }
    public JobTimestamp getJobTimestamp() { return jobTimestamp; }
    public String getProfileLeft() { return profileLeft; }
    public String getProfileRight() { return profileRight; }
    public SortKeys getSortKeys() { return sortKeys; }
    public Store getStore() { return store; }
    public Double getThreshold() {
        return threshold;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(KEY_backup, backup);
        m.put(KEY_baseDir, baseDir);
        m.put(KEY_cleanOlderThan, cleanOlderThan);
        m.put(KEY_ignoreMetadataKeys, ignoreMetadataKeys);
        m.put(KEY_jobName, jobName);
        m.put(KEY_jobTimestamp, jobTimestamp);
        m.put(KEY_profileLeft, profileLeft);
        m.put(KEY_profileRight, profileRight);
        m.put(KEY_sortKeys, sortKeys);
        m.put(KEY_store, store);
        m.put(KEY_threshold, threshold);
        return m;
    }

    public static class Builder {
        private Path baseDir;
        private Store store;
        private Store backup;
        private JobName jobName;
        private JobTimestamp jobTimestamp;
        private String profileLeft;
        private String profileRight;
        private SortKeys sortKeys;
        private Double threshold;
        private IgnoreMetadataKeys ignoreMetadataKeys;
        private JobTimestamp cleanOlderThan;

        public Builder() {
            this.baseDir = null;
            this.store = Store.NULL_OBJECT;
            this.backup = Store.NULL_OBJECT;
            this.jobName = JobName.NULL_OBJECT;
            this.jobTimestamp = JobTimestamp.NULL_OBJECT;
            this.profileLeft = "";
            this.profileRight = "";
            this.sortKeys = SortKeys.NULL_OBJECT;
            this.threshold = 0.0;
            this.ignoreMetadataKeys = IgnoreMetadataKeys.NULL_OBJECT;
            this.cleanOlderThan = JobTimestamp.now().minusHours(3);
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
        public Builder profileLeft(String pf) {
            this.profileLeft = pf;
            return this;
        }
        public Builder profileRight(String pf) {
            this.profileRight = pf;
            return this;
        }
        public Builder sortKeys(SortKeys sortKeys) {
            this.sortKeys = sortKeys;
            return this;
        }
        public Builder threshold(Double threshold) throws InspectusException {
            if (threshold < 0.0) {
                throw new InspectusException("threshold must be >= 0.0");
            }
            this.threshold = threshold;
            return this;
        }
        public Builder ignoreMetadataKeys(IgnoreMetadataKeys ignoreMetadataKeys) {
            this.ignoreMetadataKeys = ignoreMetadataKeys;
            return this;
        }
        public Builder cleanOlderThan(JobTimestamp cleanOlderThan) {
            this.cleanOlderThan = cleanOlderThan;
            return this;
        }
        public Parameters build() {
            return new Parameters(this);
        }
    }
}
