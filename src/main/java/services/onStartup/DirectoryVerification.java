package services.onStartup;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DirectoryVerification {
    public static void verifyDirectoryIntegrity(Logger LOGGER) {
        ArrayList<File> directories = new ArrayList<>();
        directories.add(new File("DB"));
        directories.add(new File("resources"));
        directories.add(new File("resources/logs"));
        directories.add(new File("resources/purge"));
        directories.add(new File("resources/duckies"));
        directories.add(new File("tempFiles"));
        directories.add(new File("tempFiles/place"));
        directories.add(new File("tempFiles/place/queue"));
        directories.add(new File("tempFiles/place/encode"));

        for (File directory : directories) {
            if (!directory.isDirectory()) {
                directory.mkdir();
                LOGGER.info("Directory \"" + directory.getPath() + "\" has been created");
            }
        }
    }

    public static void verifyFileIntegrity(Logger LOGGER) {
        HashMap<String, ArrayList<String>> files = new HashMap<>();
        files.put("DB/", new ArrayList<>(List.of("RubberDucky.db")));
        files.put("resources/", new ArrayList<>(List.of("shutdown.gif", "sudoku.jpg", "nuke.gif")));
        files.put("resources/duckies/", new ArrayList<>(List.of("ducky0.png", "ducky1.png", "ducky2.png",
                "ducky3.png", "ducky4.png", "ducky5.png", "ducky6.png", "ducky7.png")));
        files.put("resources/purge/", new ArrayList<>(List.of("busyPurging.png", "purgeCommenced.jpg",
                "purgeEnded.jpg")));
        String url;
        try {
            Scanner scanner = new Scanner(new File("url.txt"));
            url = scanner.nextLine();
            scanner.close();
        } catch (Exception e) {
            LOGGER.warn("There does not exist a file with the name:  url.txt");
            return;
        }

        for (String directory : files.keySet()) {
            for (String file : files.get(directory)) {
                File current = new File(directory + file);
                if (!current.exists()) {
                    try {
                        ReadableByteChannel byteChannel = Channels.newChannel(new URL(url + file).openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(directory + file);
                        fileOutputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
                        fileOutputStream.close();
                    } catch (Exception e) {
                        LOGGER.error("Problem while downloading file: " + file, e);
                    }
                    LOGGER.info("Downloaded file: " + directory + file);
                }
            }
        }
    }
}
