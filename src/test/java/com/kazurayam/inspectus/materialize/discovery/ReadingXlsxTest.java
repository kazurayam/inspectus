package com.kazurayam.inspectus.materialize.discovery;

import com.kazurayam.inspectus.zest.TestOutputOrganizerFactory;
import com.kazurayam.unittest.TestOutputOrganizer;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ReadingXlsxTest {

    private static final TestOutputOrganizer too =
            TestOutputOrganizerFactory.create(ReadingXlsxTest.class);
    Path fixturesDir = too.getProjectDirectory().resolve("src/test/fixtures");
    Path fixtureDir = fixturesDir.resolve("com/kazurayam/inspectus/materialize/discovery/ReadingXlsxTest");

    @Test
    public void test_read_XSLS_file() throws IOException {
        String sheetName = "Sheet1";
        String tableName = "テーブル1";
        Path xlsx = fixtureDir.resolve("names.xlsx");
        assert Files.exists(xlsx): String.format("%s is not present", xlsx.toString());
        FileInputStream fis = new FileInputStream(xlsx.toFile());
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet(sheetName);
        assert sheet != null;
        List<XSSFTable> tables = sheet.getTables();
        if (tables.size() == 0) {
            throw new IOException("The sheet contains not Table");
        }
        XSSFTable table = null;
        if (tables.size() == 1) {
            table = tables.get(0);
        } else {
            List<XSSFTable> tablesWithTheName =
                    tables.stream()
                            .filter( t -> t.getName().equals(tableName))
                            .collect(Collectors.toList());
            if (tablesWithTheName.size() == 0) {
                throw new IllegalStateException(
                        String.format("no table named \"%s\" in the sheet named \"%s\"",
                                tableName, sheetName));
            }
            if (tablesWithTheName.size() > 1) {
                throw new IllegalStateException(
                        String.format("there are 2 or more tables named \"%s\" in the sheet named \"%s\"",
                                tableName, sheetName));
            }
            table = tablesWithTheName.get(0);
        }
    }
}
