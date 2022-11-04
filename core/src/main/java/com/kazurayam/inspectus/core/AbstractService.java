package com.kazurayam.inspectus.core;

import com.google.gson.Gson;
import com.kazurayam.inspectus.util.GsonHelper;
import com.kazurayam.materialstore.base.manage.StoreCleaner;
import com.kazurayam.materialstore.base.manage.StoreExport;
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup;
import com.kazurayam.materialstore.base.report.IndexCreator;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.MaterialList;
import com.kazurayam.materialstore.core.filesystem.MaterialstoreException;
import com.kazurayam.materialstore.core.filesystem.SortKeys;
import com.kazurayam.materialstore.core.filesystem.Store;
import com.kazurayam.materialstore.base.manage.StoreImport;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractService implements Inspectus {

    public AbstractService() {
        super();
    }

    /**
     * import the lastest artifacts from the remote/backup store
     * into the local/current store
     * @param parameters must contain the keys of "store", "back" and "jobName"
     * @throws InspectusException
     */
    @Override
    public final void preProcess(Map<String, Object> parameters) throws InspectusException {
        step0_restorePrevious(parameters);
    }

    @Override
    public void postProcess(Map<String, Object> parameters, Map<String, Object> intermediates)
            throws InspectusException {
        step4_report(parameters, intermediates);
        step5_backupLatest(parameters, intermediates);
        step6_cleanup(parameters, intermediates);
        step7_index(parameters, intermediates);
    }

    protected final void step0_restorePrevious(Map<String, Object> parameters)
            throws InspectusException {
        Store backup = getBackup(parameters);
        Store store = getStore(parameters);
        JobName jobName = getJobName(parameters);
        try {
            StoreImport importer = StoreImport.newInstance(backup, store);
            importer.importReports(jobName);
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }


    abstract protected void step4_report(Map<String, Object> parameters, Map<String, Object> intermediates)
            throws InspectusException;

    protected void step5_backupLatest(Map<String, Object> parameters, Map<String, Object> intermediates)
            throws InspectusException {
        Store store = getStore(parameters);
        JobName jobName = getJobName(parameters);
        Store backup = getBackup(parameters);
        try {
            StoreExport export = StoreExport.newInstance(store, backup);
            export.exportReports(jobName);
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }

    protected void step6_cleanup(Map<String, Object> parameters, Map<String, Object> intermediates)
            throws InspectusException {
        Store store = getStore(parameters);
        JobName jobName = getJobName(parameters);
        try {
            StoreCleaner cleaner = StoreCleaner.newInstance(store);
            cleaner.cleanup(jobName, JobTimestamp.now().minusHours(3));
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }

    protected void step7_index(Map<String, Object> parameters, Map<String, Object> intermediates)
            throws InspectusException {
        Store store = getStore(parameters);
        try {
            IndexCreator indexCreator = new IndexCreator(store);
        } catch (MaterialstoreException e) {
            throw new InspectusException(e);
        }
    }

    //-------------------------------------------------------------------------

    public static final String KEY_store = "store";
    protected static Store getStore(Map<String, Object> p)
            throws InspectusException {
        Objects.requireNonNull(p);
        String k = KEY_store;
        if (!p.containsKey(k)) {
            throw new InspectusException(msg1(k, p));
        }
        if (!(p.get(k) instanceof Store)) {
            throw new InspectusException(
                    msg2(k, p.get(k), MaterialList.class, p.get(k).getClass()));
        }
        return (Store)p.get(k);
    }

    public static final String KEY_backup = "backup";
    protected static Store getBackup(Map<String, Object> p)
            throws InspectusException {
        Objects.requireNonNull(p);
        String k = KEY_backup;
        if (!p.containsKey(k)) {
            throw new InspectusException(msg1(k, p));
        }
        if (!(p.get(k) instanceof Store)) {
            throw new InspectusException(
                    msg2(k, p.get(k), MaterialList.class, p.get(k).getClass()));
        }
        return (Store)p.get(k);
    }

    public static final String KEY_jobName = "jobName";
    protected static JobName getJobName(Map<String, Object> p)
            throws InspectusException {
        Objects.requireNonNull(p);
        String k = KEY_jobName;
        if (!p.containsKey(k)) {
            throw new InspectusException(msg1(k, p));
        }
        if (!(p.get(k) instanceof JobName)) {
            throw new InspectusException(
                    msg2(k, p.get(k), MaterialList.class, p.get(k).getClass()));
        }
        return (JobName)p.get(k);
    }

    public static final String KEY_jobTimestamp = "jobTimestamp";
    protected static JobTimestamp getJobTimestamp(Map<String, Object> p)
            throws InspectusException {
        Objects.requireNonNull(p);
        String k = KEY_jobTimestamp;
        if (!p.containsKey(k)) {
            throw new InspectusException(msg1(k, p));
        }
        if (!(p.get(k) instanceof JobTimestamp)) {
            throw new InspectusException(
                    msg2(k, p.get(k), MaterialList.class, p.get(k).getClass()));
        }
        return (JobTimestamp)p.get(k);
    }

    public static final String KEY_materialList = "materialList";
    protected static MaterialList getMaterialList(Map<String, Object> p)
            throws InspectusException {
        Objects.requireNonNull(p);
        String k = KEY_materialList;
        if (!p.containsKey(k)) {
            throw new InspectusException(msg1(k, p));
        }
        if (!(p.get(k) instanceof MaterialList)) {
            throw new InspectusException(
                    msg2(k, p.get(k), MaterialList.class, p.get(k).getClass()));
        }
        return (MaterialList)p.get(k);
    }

    public static final String KEY_sortKeys = "sortKeys";
    protected static SortKeys getSortKeys(Map<String, Object> p) throws InspectusException {
        Objects.requireNonNull(p);
        String k = KEY_sortKeys;
        if (!p.containsKey(k)) {
            throw new InspectusException(msg1(k, p));
        }
        if (!(p.get(k) instanceof SortKeys)) {
            throw new InspectusException(
                    msg2(k, p.get(k), SortKeys.class, p.get(k).getClass()));
        }
        return (SortKeys)p.get(k);
    }

    public static final String KEY_MaterialProductGroup = "materialProductGroup";
    protected static MaterialProductGroup getMaterialProductGroup(Map<String, Object> p)
        throws InspectusException {
        Objects.requireNonNull(p);
        String k = KEY_MaterialProductGroup;
        if (!p.containsKey(k)) {
            throw new InspectusException(msg1(k, p));
        }
        if (!(p.get(k) instanceof MaterialProductGroup)) {
            throw new InspectusException(
                    msg2(k, p.get(k), MaterialProductGroup.class, p.get(k).getClass()));
        }
        return (MaterialProductGroup)p.get(k);
    }

    public static final String KEY_criteria = "criteria";
    protected static Double getCriteria(Map<String, Object> p) throws InspectusException {
        Objects.requireNonNull(p);
        String k = KEY_criteria;
        if (!p.containsKey(k)) {
            throw new InspectusException(msg1(k, p));
        }
        if (!(p.get(k) instanceof MaterialProductGroup)) {
            throw new InspectusException(
                    msg2(k, p.get(k), MaterialProductGroup.class, p.get(k).getClass()));
        }
        return (Double)p.get(k);
    }

    private static String msg1(String key, Map<String, Object> parameters) {
        Gson gson = GsonHelper.createGson(false);
        String stringified = gson.toJson(GsonHelper.toStringStringMap(parameters));
        return String.format("the '%s' parameter is not found in the parameters",
                key, stringified);
    }

    private static String msg2(String key, Object value,
                               Class<?> expectedClass, Class<?> actualClass) {
        return String.format("key=%s,value=%s expected the value of %s but actually was %s",
                key, value.toString(),
                expectedClass.getName(), actualClass.getName());
    }
}
