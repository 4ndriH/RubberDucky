package services.startup;

import assets.Config;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectoryVerification {
    public static void verifyDirectoryIntegrity(Logger LOGGER) {
        ArrayList<File> directories = new ArrayList<>();
        boolean directoryCreated = false;

        if (!Config.DIRECTORY_PATH.isEmpty()) {
            directories.add(new File(Config.DIRECTORY_PATH));
        }

        directories.add(new File(Config.DIRECTORY_PATH + "DB"));
        directories.add(new File(Config.DIRECTORY_PATH + "logs"));
        directories.add(new File(Config.DIRECTORY_PATH + "resources"));
        directories.add(new File(Config.DIRECTORY_PATH + "resources/images"));
        directories.add(new File(Config.DIRECTORY_PATH + "resources/images/purge"));
        directories.add(new File(Config.DIRECTORY_PATH + "resources/images/duckies"));
        directories.add(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy"));
        directories.add(new File(Config.DIRECTORY_PATH + "tempFiles"));
        directories.add(new File(Config.DIRECTORY_PATH + "tempFiles/place"));
        directories.add(new File(Config.DIRECTORY_PATH + "tempFiles/place/queue"));
        directories.add(new File(Config.DIRECTORY_PATH + "tempFiles/place/timelapse"));

        for (File directory : directories) {
            if (!directory.isDirectory()) {
                if (directory.mkdir()) {
                    directoryCreated = true;
                    LOGGER.info("Directory \"" + directory.getPath() + "\" has been created");
                } else {
                    LOGGER.error("Directory: \"" + directory.getPath() + "\" could not be created");
                }
            }
        }

        if (!directoryCreated) {
            LOGGER.info("Directory verification completed");
        }
    }

    public static void verifyFileIntegrity(Logger LOGGER) {
        HashMap<String, ArrayList<String>> files = new HashMap<>();
        boolean fileDownloaded = false;

        files.put("DB/", new ArrayList<>());
        files.put("resources/images/",
                new ArrayList<>(List.of(
                    "nuke.gif",
                    "shutdown.gif",
                    "sudoku.jpg"
                )));
        files.put("resources/images/duckies/",
                new ArrayList<>(List.of(
                        "ducky0.png",
                        "ducky1.png",
                        "ducky2.png",
                        "ducky3.png",
                        "ducky4.png",
                        "ducky5.png",
                        "ducky6.png",
                        "ducky7.png"
                )));
        files.put("resources/images/purge/",
                new ArrayList<>(List.of(
                        "busyPurging.png",
                        "purgeCommenced.jpg",
                        "purgeEnded.jpg"
                )));
        files.put("resources/images/lmgtfy/",
                new ArrayList<>(List.of(
                        "lmgtfy.png",
                        "lmgtfyResult.png",
                        "lmgtfyFix.png",
                        "cursor0.png",
                        "cursor1.png",
                        "cursor2.png",
                        "cursor3.png",
                        "cursor4.png",
                        "cursor5.png",
                        "cursor6.png",
                        "cursor7.png",
                        "cursor8.png",
                        "cursor9.png",
                        "cursor10.png"
                )));

        // change this to the non branch link
        String url = "https://raw.githubusercontent.com/4ndriH/RubberDucky/master/";

        for (String directory : files.keySet()) {
            for (String file : files.get(directory)) {
                File current = new File(Config.DIRECTORY_PATH + directory + file);
                if (!current.exists()) {
                    try {
                        ReadableByteChannel byteChannel = Channels.newChannel(new URL(url + Config.DIRECTORY_PATH + directory + file).openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(Config.DIRECTORY_PATH + directory + file);
                        fileOutputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
                        fileOutputStream.close();
                        fileDownloaded = true;
                    } catch (Exception e) {
                        LOGGER.error("There was a problem while downloading file: " + file, e);
                        continue;
                    }
                    LOGGER.info("Downloaded file: " + directory + file);
                }
            }
        }

        if (!fileDownloaded) {
            LOGGER.info("File Verification completed");
        }
    }
}
