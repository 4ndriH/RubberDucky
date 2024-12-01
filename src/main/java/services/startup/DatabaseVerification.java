package services.startup;

import assets.objects.Pixel;
import org.slf4j.Logger;
import services.database.daos.*;
import services.database.entities.ChannelMessageTrafficORM;
import services.database.entities.PlaceThroughputLogORM;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DatabaseVerification {
    public static void importSqliteData(Logger LOGGER) {
        LOGGER.info("Importing sqlite data");
        Scanner scanner;
        String table = "";
        HashMap<Integer, ArrayList<Pixel>> placePixels = new HashMap<>();
        ArrayList<PlaceThroughputLogORM> placeThroughput = new ArrayList<>();
        ArrayList<ChannelMessageTrafficORM> channelMessageTraffic = new ArrayList<>();

        try {
            scanner = new Scanner(new File("bot_data/sqlite.sql"));
        } catch (FileNotFoundException e) {
            LOGGER.error("sqlite.sql not found");
            return;
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("CREATE TABLE IF NOT EXISTS")) {
                Pattern pattern = Pattern.compile("\"(.*?)\"");
                Matcher matcher = pattern.matcher(line);

                matcher.find();
                table = matcher.group(1);

                LOGGER.info("Table: {}", table);
            } else if (line.contains("CREATE TABLE PlaceEfficiencyLog(")) {
                table = "PlaceEfficiencyLog";
                LOGGER.info("Table: {}", table);
            } else if (line.contains("CREATE TABLE EfficiencyLog(")) {
                table = "EfficiencyLog";
                LOGGER.info("Table: {}", table);
            } else if (table.equals("PlacePixels") && line.contains("INSERT INTO")) {
                String[] values = insertMatcher(line).split(",");

                int placeId = Integer.parseInt(values[0]);
                int x = Integer.parseInt(values[2]);
                int y = Integer.parseInt(values[3]);
                String color = values[4];
                double alpha = Double.parseDouble(values[5]);

                placePixels.putIfAbsent(placeId, new ArrayList<>());

                placePixels.get(placeId).add(new Pixel(x, y, alpha, color));
            } else if (table.equals("PlaceProjects") && line.contains("INSERT INTO")) {
                String[] values = insertMatcher(line).split(",");

                int projectId = Integer.parseInt(values[0]);
                int progress = Integer.parseInt(values[1]);
                String userId = values[2];

                PlaceProjectsDAO dao = new PlaceProjectsDAO();
                dao.queueProject(userId, projectId);
                dao.updateProjectProgress(projectId, progress);
            } else if (table.equals("BlacklistedUsers") && line.contains("INSERT INTO")) {
                String[] values = insertMatcher(line).split(",");

                String userId = values[0];

                UsersDAO dao = new UsersDAO();
                dao.addUser(userId);
                dao.toggleUserBlacklist(userId);
            } else if (table.equals("Config") && line.contains("INSERT INTO")) {
                String[] values = insertMatcher(line).split(",");

                String key = values[0];
                String value = values[1];
                String type = "STRING";

                if (value.equals("true") || value.equals("false")) {
                    type = "BOOLEAN";
                } else if (value.matches("^[0-9]+$") && value.length() < 8) {
                    type = "INTEGER";
                }

                ConfigDAO dao = new ConfigDAO();
                dao.addConfigEntryIfNotExists(key, value, type);
            } else if (table.equals("PlaceEfficiencyLog") && line.contains("INSERT INTO")) {
                String[] values = insertMatcher(line).split(",");

                int batchSize = Integer.parseInt(values[1]);
                int batchTime = Integer.parseInt(values[2]);
                String timeStamp = values[3];

                PlaceThroughputLogORM temp = new PlaceThroughputLogORM();
                temp.setBatchSize(batchSize);
                temp.setMessageBatchTime(batchTime);
                temp.setCreatedAt(timeStampToUnix(timeStamp));

                placeThroughput.add(temp);
            } else if (table.equals("EfficiencyLog") && line.contains("INSERT INTO") && !line.contains("sqlite_sequence")) {
                String[] values = insertMatcher(line).split(",");

                long seconds = Long.parseLong(values[0]);
                int place = Integer.parseInt(values[1]);
                int count = Integer.parseInt(values[2]);

                ChannelMessageTrafficORM temp = new ChannelMessageTrafficORM();
                temp.setCountThread(count);
                temp.setEthPlaceBots(place);
                temp.setCreatedAt(seconds);

                channelMessageTraffic.add(temp);
            }
        }

        LOGGER.info("Importing {} place throughput log entries", placeThroughput.size());
        (new PlaceThroughputLogDAO()).importLogEntry(placeThroughput);

        LOGGER.info("Importing {} channel message traffic entries", channelMessageTraffic.size());
        (new ChannelMessageTrafficDAO()).importLogEntry(channelMessageTraffic);

        PlacePixelsDAO dao = new PlacePixelsDAO();
        for (int placeId : placePixels.keySet()) {
            LOGGER.info("Importing place pixels for project {}", placeId);
            dao.queuePixels(placeId, placePixels.get(placeId));
        }

        File file = new File("bot_data/sqlite.sql");
        if (file.delete()) {
            LOGGER.info("sqlite.sql deleted");
        } else {
            LOGGER.error("Failed to delete sqlite.sql");
        }
    }

    private static String insertMatcher(String line) {
        line = line.replaceAll("'", "");
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(line);
        matcher.find();
        return matcher.group(1);
    }

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static long timeStampToUnix(String timeStamp) {
        ZonedDateTime zonedDateTime = LocalDateTime.parse("2024-08-18 15:26:28", formatter).atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant().toEpochMilli();
    }
}
