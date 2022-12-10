package com.kazurayam.inspectus.materialize.url;

import com.kazurayam.inspectus.materialize.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class URLDownloaderTest {

    private static Path testCaseOutputDir;

    @BeforeAll
    public static void beforeAll() throws IOException {
        testCaseOutputDir =
                TestHelper.createTestClassOutputDir(URLDownloaderTest.class);
    }

    @BeforeEach
    public void setup() {
    }

    @Test
    public void test_download() throws IOException {
        URL url = new URL("https://www.data.jma.go.jp/rss/jma.rss");
        Path out = testCaseOutputDir.resolve("jma.rss");
        URLDownloader.download(url, out);
        Assertions.assertTrue(Files.exists(out));
        Assertions.assertTrue(out.toFile().length() > 0);
    }

}
