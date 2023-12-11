package com.kazurayam.inspectus.materialize.url;

import com.kazurayam.inspectus.zest.TestOutputOrganizerFactory;
import com.kazurayam.unittest.TestOutputOrganizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class URLDownloaderTest {

    private static final TestOutputOrganizer too =
            TestOutputOrganizerFactory.create(URLDownloaderTest.class);
    private static Path classOutputDir;

    @BeforeAll
    public static void beforeAll() throws IOException {
        too.cleanClassOutputDirectory();
        classOutputDir = too.getClassOutputDirectory();
    }

    @Test
    public void test_download() throws IOException {
        URL url = new URL("https://www.data.jma.go.jp/rss/jma.rss");
        Path out = classOutputDir.resolve("jma.rss");
        URLDownloader.download(url, out);
        Assertions.assertTrue(Files.exists(out));
        Assertions.assertTrue(out.toFile().length() > 0);
    }

}
