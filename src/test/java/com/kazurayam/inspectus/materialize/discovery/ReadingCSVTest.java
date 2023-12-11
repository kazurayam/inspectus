package com.kazurayam.inspectus.materialize.discovery;

import com.kazurayam.inspectus.zest.TestOutputOrganizerFactory;
import com.kazurayam.unittest.TestOutputOrganizer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public class ReadingCSVTest {

    private static final TestOutputOrganizer too =
            TestOutputOrganizerFactory.create(ReadingCSVTest.class);
    private static final Path fixturesDir =
            too.getProjectDir().resolve("src/test/fixtures");
    private static final Path fixtureDir =
            fixturesDir.resolve("com/kazurayam/inspectus/materialize/discovery/ReadingCSVTest");

    @Test
    public void test_read_CSV_file() throws IOException {
        Path csv = fixtureDir.resolve("names.csv");
        Reader in = new FileReader(csv.toFile());
        CSVFormat csvFormat =
                CSVFormat.RFC4180.builder().setHeader()
                        .setSkipHeaderRecord(true).build();
        Iterable<CSVRecord> records = csvFormat.parse(in);
        for (CSVRecord record : records) {
            String lastName = record.get("Last Name");
            String firstName = record.get("First Name");
            System.out.printf("lastName=\"%s\", firstName=\"%s\"\n",
                    lastName, firstName);
        }
    }
}
