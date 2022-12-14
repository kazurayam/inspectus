package com.kazurayam.inspectus.materialize.url;

import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.Metadata;
import com.kazurayam.materialstore.core.Store;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public final class URLMaterializer {
    public URLMaterializer(Store store) {
        this.store = store;
    }

    public Material materialize(URL url, JobName jobName, JobTimestamp jobTimestamp, FileType fileType) throws IOException, MaterialstoreException {
        Objects.requireNonNull(url);
        Objects.requireNonNull(jobName);
        Objects.requireNonNull(jobTimestamp);
        Objects.requireNonNull(fileType);
        Path tempFile = Files.createTempFile(null, null);
        InputStream inputStream = url.openStream();
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        Metadata metadata = Metadata.builder(url).build();
        Material material = store.write(jobName, jobTimestamp, fileType, metadata, tempFile);
        return material;
    }

    private Store store;
}
