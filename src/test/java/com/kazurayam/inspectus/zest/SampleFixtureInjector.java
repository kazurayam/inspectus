package com.kazurayam.inspectus.zest;

import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.Metadata;
import com.kazurayam.materialstore.core.Store;
import com.kazurayam.unittest.TestOutputOrganizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class SampleFixtureInjector {

    private static final TestOutputOrganizer too =
            TestOutputOrganizerFactory.create(SampleFixtureInjector.class);
    private static final Path fixturesDir =
            too.getProjectDir().resolve("src/test/fixtures");

    private SampleFixtureInjector() {}

    public static JobTimestamp create3TXTs(Store store,
                                           JobName jobName,
                                           JobTimestamp jobTimestamp)
            throws MaterialstoreException {
        Material apple = writeTXT(store, jobName, jobTimestamp, "Apple", "01", "it is red");
        Material mikan = writeTXT(store, jobName, jobTimestamp, "Mikan", "02", "it is orange");
        Material money = writeTXT(store, jobName, jobTimestamp, "Money", "03", "it is green");
        return jobTimestamp;
    }

    public static JobTimestamp create3PNGs(Store store,
                                           JobName jobName,
                                           JobTimestamp jobTimestamp)
            throws MaterialstoreException {
        Material apple = writePNG(store, jobName, jobTimestamp,
                fixturesDir.resolve("apple_mikan_money/apple.png"), "01", "it is red");
        Material mikan = writePNG(store, jobName, jobTimestamp,
                fixturesDir.resolve("apple_mikan_money/mikan.png"), "02", "it is orange");
        Material money = writePNG(store, jobName, jobTimestamp,
                fixturesDir.resolve("apple_mikan_money/money.png"), "03", "it is green");
        return jobTimestamp;
    }

    private static Material writeTXT(Store store,
                                     JobName jobName,
                                     JobTimestamp jobTimestamp,
                                     String text,
                                     String step,
                                     String label) throws MaterialstoreException {
        Metadata metadata =
                new Metadata.Builder()
                        .put("step", step)
                        .put("label", label).build();
        return store.write(jobName, jobTimestamp, FileType.TXT, metadata, text);
    }

    private static Material writePNG(Store store,
                                     JobName jobName,
                                     JobTimestamp jobTimestamp,
                                     Path png,
                                     String step,
                                     String label) throws MaterialstoreException {
        Metadata metadata =
                new Metadata.Builder()
                        .put("step", step)
                        .put("label", label).build();
        return store.write(jobName, jobTimestamp, FileType.PNG, metadata, png);
    }

    /*
     * returns BufferedImage an image of red apple on which the current timestamp is placed
     * https://stackoverflow.com/questions/2736320/write-text-onto-image-in-java
     */
    public static BufferedImage createAppleImage(JobTimestamp jobTimestamp) throws IOException {
        Path images = fixturesDir.resolve("images");
        Path apple = images.resolve("apple.png");
        BufferedImage bufferedImage = ImageIO.read(apple.toFile());
        Graphics g = bufferedImage.getGraphics();
        //g.setColor(Color.LIGHT_GRAY);
        //g.fillRect(500, 600, 200, 50);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial Black", Font.BOLD, 80));
        g.drawString(jobTimestamp.toString(), 420, 660);
        return bufferedImage;
    }
}