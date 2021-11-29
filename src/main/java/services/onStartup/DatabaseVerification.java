package services.onStartup;

import org.slf4j.Logger;
import services.database.DatabaseHandler;

import java.io.File;
import java.util.*;

public class DatabaseVerification {
    public static void verifyDatabaseIntegrity(Logger LOGGER) {
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

    public static void databaseValueImport(Logger LOGGER) {
        Scanner scanner;
        File txt = new File("dbImport.txt");

        try {
            scanner = new Scanner(txt);
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

        txt.deleteOnExit();
        LOGGER.info("Database import has been completed!");
        scanner.close();
    }
}
