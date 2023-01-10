package com.kazurayam.inspectus.materialize.url;

import com.kazurayam.inspectus.materialize.TestHelper;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.materialstore.core.Stores;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class URLMaterializerTest {

    private static Store store;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Path outputDir =
                TestHelper.createTestClassOutputDir(URLDownloaderTest.class);
        Path root = outputDir.resolve("root");
        store = Stores.newInstance(root);
    }

    @BeforeEach
    public void setup() {
    }

    @Test
    public void test_materialize() throws IOException, MaterialstoreException {
        URL url = new URL("https://www.data.jma.go.jp/rss/jma.rss");
        JobName jobName = new JobName("test_materialize");
        JobTimestamp jobTimestamp = JobTimestamp.now();
        FileType fileType = FileType.XML;
        URLMaterializer materializer = new URLMaterializer(store);
        Material material = materializer.materialize(url, jobName, jobTimestamp, fileType);
        Assertions.assertNotNull(material);
        Path file = material.toPath();
        Assertions.assertTrue(Files.exists(file));
        Assertions.assertTrue(file.toFile().length() > 0);
    }


}
