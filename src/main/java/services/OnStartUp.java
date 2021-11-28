package services;

import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

public class OnStartUp {
    private final Logger LOGGER = LoggerFactory.getLogger(OnStartUp.class);

    public OnStartUp() {
        DirectoryVerification();
        FileVerification();
        DataBaseVerification();
        importValuesToDB();
        updateToken();
        DatabaseHandler.incrementStartUpCounter();
        CONFIG.reload();
        MessageCleanUp();
    }

    private void MessageCleanUp () {
        (new Thread(() -> {
            while(CONFIG.instance == null) {
                try {
                    Thread.sleep(32768);
                } catch (InterruptedException ignored) {}
            }

            ArrayList<ArrayList<String>> msgs = DatabaseHandler.getDeleteMessages();
            for (int i = 0; i < msgs.get(0).size(); i++) {
                try {
                    CONFIG.instance.getGuildById(msgs.get(0).get(i))
                            .getTextChannelById(msgs.get(1).get(i))
                            .deleteMessageById(msgs.get(2).get(i)).queue(
                                    null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)
                            );
                    Thread.sleep(1024);
                } catch (Exception ignored) {}
            }
        })).start();
    }

    private void DirectoryVerification() {
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

    private void FileVerification () {
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

    private void DataBaseVerification() {
        HashMap<String, String> database = new HashMap<>();
        database.put("channels", "id\tINTEGER NOT NULL,\n" +
                "\tcommand\tTEXT NOT NULL,\n" +
                "\tchannel\tTEXT NOT NULL,\n" +
                "\tPRIMARY KEY(id AUTOINCREMENT)");
        database.put("config", "key\tTEXT NOT NULL UNIQUE,\n" +
                "\tvalue\tTEXT NOT NULL,\n" +
                "\tPRIMARY KEY(key)");
        database.put("deleteMsgs", "key\tINTEGER NOT NULL,\n" +
                "\tserver\tTEXT NOT NULL,\n" +
                "\tchannel\tTEXT NOT NULL,\n" +
                "\tid\tTEXT NOT NULL,\n" +
                "\tdeleteTime\tINTEGER NOT NULL,\n" +
                "\tinstanceNr\tINTEGER,\n" +
                "\tPRIMARY KEY(key AUTOINCREMENT)");
        database.put("place_queue", "key\tINTEGER NOT NULL UNIQUE,\n" +
                "\tfile\tTEXT NOT NULL,\n" +
                "\tprogress\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\tuser\tTEXT NOT NULL,\n" +
                "\tPRIMARY KEY(key)");
        database.put("servers", "server\tTEXT NOT NULL UNIQUE");
        database.put("userBlacklist", "user\tTEXT NOT NULL UNIQUE");

        for (String table : database.keySet()) {
            if (DatabaseHandler.createTableIfNotExists(table, database.get(table))) {
                LOGGER.info("Table " + table + " has been added to the database");
            }
        }
    }

    private void importValuesToDB() {
        Scanner scanner;
        try {
            scanner = new Scanner(new File("dbImport.txt"));
        } catch (Exception e) {
            return;
        }

        String table = null;
        List<String> columns = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("===")) {
                table = line.replace("===", "");
                columns = new ArrayList<>();
            } else if (columns.size() == 0) {
                columns = Arrays.asList(line.split("\t"));
            } else if (!line.equals("---------------------------------") && table != null) {
                String[] split = line.split("\t");
                switch (table) {
                    case "config":
                        DatabaseHandler.insertConfig(split[0], split[1]);
                        break;
                    case "channels":
                        DatabaseHandler.insertChannel(split[1], split[2]);
                        break;
                    case "servers":
                        DatabaseHandler.insertServer(split[0]);
                        break;
                    case "userBlacklist":
                        DatabaseHandler.insertBlacklist(split[0]);
                        break;
                }
            }
        }

        LOGGER.info("Database import has been completed!");
        scanner.close();
    }

    private void updateToken() {
        Scanner scanner;
        try {
            scanner = new Scanner(new File("token.txt"));
        } catch (Exception e) {
            return;
        }

        DatabaseHandler.updateConfig("token", scanner.next());
        LOGGER.warn("The token has been updated!");
        scanner.close();
    }
}
