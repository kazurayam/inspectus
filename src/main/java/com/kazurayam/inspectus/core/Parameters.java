package com.kazurayam.inspectus.core;

import com.kazurayam.materialstore.base.reduce.zipper.DiffColor;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.SortKeys;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.core.metadata.IgnoreMetadataKeys;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Parameters {

    public static final Parameters NULL_OBJECT = new Builder().build();
    private final Path baseDir;
    private final JobTimestamp baselinePriorTo;
    private final Store store;
    private final Store backup;
    private final JobName jobName;
    private final JobTimestamp jobTimestamp;
    private final SortKeys sortKeys;
    private final Double threshold;
    private final IgnoreMetadataKeys ignoreMetadataKeys;
    private final JobTimestamp cleanOlderThan;
    private final Environment environment;
    private final DiffColor withDiffColor;

    public static final String KEY_baseDir = "baseDir";
    public static final String KEY_baselinePriorTo = "baselinePriorTo";
    public static final String KEY_store = "store";
    public static final String KEY_backup = "backup";
    public static final String KEY_cleanOlderThan = "cleanOlderThan";
    public static final String KEY_jobName = "jobName";
    public static final String KEY_jobTimestamp = "jobTimestamp";
    public static final String KEY_sortKeys = "sortKeys";
    public static final String KEY_threshold = "threshold";
    public static final String KEY_ignoreMetadataKeys = "ignoreMetadataKeys";
    public static final String KEY_environment = "environment";
    public static final String KEY_withDiffColor = "withDiffColor";

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Parameters source) {
        return new Builder(source);
    }

    private Parameters(Builder b) {
        this.baseDir = b.baseDir;
        this.baselinePriorTo = b.baselinePriorTo;
        this.store = b.store;
        this.backup = b.backup;
        this.jobName = b.jobName;
        this.jobTimestamp = b.jobTimestamp;
        this.sortKeys = b.sortKeys;
        this.threshold = b.threshold;
        this.ignoreMetadataKeys = b.ignoreMetadataKeys;
        this.cleanOlderThan = b.cleanOlderThan;
        this.environment = b.environment;
        this.withDiffColor = b.withDiffColor;
    }
    public Boolean containsBackup() { return backup != Store.NULL_OBJECT;}
    public Boolean containsBaseDir() { return baseDir != null; }
    public Boolean containsBaselinePriorTo() { return baselinePriorTo != JobTimestamp.NULL_OBJECT; }
    public Boolean containsCleanOlderThan() { return cleanOlderThan != JobTimestamp.NULL_OBJECT; }
    public Boolean containsIgnoreMetadataKeys() { return ignoreMetadataKeys != IgnoreMetadataKeys.NULL_OBJECT; }
    public Boolean containsJobName() { return jobName != JobName.NULL_OBJECT; }
    public Boolean containsJobTimestamp() { return jobTimestamp != JobTimestamp.NULL_OBJECT; }
    public Boolean containsSortKeys() { return sortKeys != null; }
    public Boolean containsStore() { return store != Store.NULL_OBJECT; }
    public Boolean containsThreshold() { return threshold >= 0.0; }
    public Boolean containsEnvironment() { return environment != Environment.NULL_OBJECT; }
    public Boolean containsWithDiffColor() { return withDiffColor != null; }

    public Store getBackup() { return backup; }
    public Path getBaseDir() { return baseDir; }
    public JobTimestamp getBaselinePriorTo() { return baselinePriorTo; }
    public JobTimestamp getCleanOlderThan() { return cleanOlderThan; }
    public IgnoreMetadataKeys getIgnoreMetadataKeys() { return ignoreMetadataKeys; }
    public JobName getJobName() { return jobName; }
    public JobTimestamp getJobTimestamp() { return jobTimestamp; }
    public SortKeys getSortKeys() { return sortKeys; }
    public Store getStore() { return store; }
    public Double getThreshold() {
        return threshold;
    }
    public Environment getEnvironment() { return environment; }
    public DiffColor getWithDiffColor() { return withDiffColor; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(KEY_backup, backup);
        m.put(KEY_baseDir, baseDir);
        m.put(KEY_baselinePriorTo, baselinePriorTo);
        m.put(KEY_cleanOlderThan, cleanOlderThan);
        m.put(KEY_ignoreMetadataKeys, ignoreMetadataKeys);
        m.put(KEY_jobName, jobName);
        m.put(KEY_jobTimestamp, jobTimestamp);
        m.put(KEY_sortKeys, sortKeys);
        m.put(KEY_store, store);
        m.put(KEY_threshold, threshold);
        m.put(KEY_environment, environment);
        m.put(KEY_withDiffColor, withDiffColor);
        return m;
    }

    public static class Builder {
        private Path baseDir;
        private JobTimestamp baselinePriorTo;
        private Store store;
        private Store backup;
        private JobName jobName;
        private JobTimestamp jobTimestamp;
        private SortKeys sortKeys;
        private Double threshold;
        private IgnoreMetadataKeys ignoreMetadataKeys;
        private JobTimestamp cleanOlderThan;
        private Environment environment;
        private DiffColor withDiffColor;

        public Builder() {
            this.baseDir = null;
            this.baselinePriorTo = JobTimestamp.NULL_OBJECT;
            this.store = Store.NULL_OBJECT;
            this.backup = Store.NULL_OBJECT;
            this.jobName = JobName.NULL_OBJECT;
            this.jobTimestamp = JobTimestamp.NULL_OBJECT;
            this.sortKeys = SortKeys.NULL_OBJECT;
            this.threshold = 0.0;
            this.ignoreMetadataKeys = IgnoreMetadataKeys.NULL_OBJECT;
            this.cleanOlderThan = JobTimestamp.now().minusHours(3);
            this.environment = Environment.NULL_OBJECT;
            this.withDiffColor = DiffColor.DEFAULT;
        }
        public Builder(Parameters source) {
            this.baseDir = source.baseDir;
            this.baselinePriorTo = source.baselinePriorTo;
            this.store = source.store;
            this.backup = source.backup;
            this.jobName = source.jobName;
            this.jobTimestamp = source.jobTimestamp;
            this.sortKeys = source.sortKeys;
            this.threshold = source.threshold;
            this.ignoreMetadataKeys = source.ignoreMetadataKeys;
            this.cleanOlderThan = source.cleanOlderThan;
            this.environment = source.environment;
            this.withDiffColor = source.withDiffColor;
        }
        public Builder baseDir(Path baseDir) {
            this.baseDir = baseDir;
            return this;
        }
        public Builder baselinePriorTo(JobTimestamp baselinePriorTo) throws InspectusException {
            if (baselinePriorTo.compareTo(JobTimestamp.now()) > 0) {
                throw new InspectusException(
                        String.format("baselinePriorTo=%s must not be newer the current timestamp=%s",
                                baselinePriorTo, JobTimestamp.now().toString())
                );
            }
            this.baselinePriorTo = baselinePriorTo;

            // FIXME:
            // making cleanOlderThan to be equal to baselinePriorTo will be
            // not preciese, but will be effective to reduce unnecessarily
            // large amount of file copies
            this.cleanOlderThan = baselinePriorTo;

            return this;
        }

        public Builder baselinePriorToOrEqualTo(JobTimestamp jt) throws InspectusException {
            return this.baselinePriorTo(jt.plusSeconds(1));
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
        public Builder cleanOlderThan(JobTimestamp cleanOlderThan) throws InspectusException {
            if (cleanOlderThan.compareTo(JobTimestamp.now()) > 0) {
                throw new InspectusException(
                        String.format("cleanOlderThan=% must not be newer than the current timestamp",
                                cleanOlderThan, JobTimestamp.now())
                );
            }
            this.cleanOlderThan = cleanOlderThan;
            return this;
        }
        public Builder environment(Environment environment) {
            this.environment = environment;
            return this;
        }
        public Builder withDiffColor(DiffColor diffColor) {
            this.withDiffColor = diffColor;
            return this;
        }

        public Parameters build() {
            return new Parameters(this);
        }
    }
}
