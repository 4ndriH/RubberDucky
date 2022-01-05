package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHandler.class);

    ////////////////////////////////////////
    // Config
    ////////////////////////////////////////
    public static void insertConfig(String key, String value) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO config (key, value) VALUES (?, ?)"
            );
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void updateConfig(String key, String value) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE config SET value = ? WHERE key = ?"
            );
            ps.setString(1, value);
            ps.setString(2, key);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static HashMap<String, String> getConfig() {
        HashMap<String, String> config = new HashMap<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM config"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                config.put(rs.getString("key"), rs.getString("value"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return config;
    }

    public static void incrementStartUpCounter() {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT value FROM config WHERE key='systemStartUps'"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                ps = connection.prepareStatement(
                        "UPDATE config SET value=? WHERE key='systemStartUps'"
                );
                instanceNr = Integer.parseInt(rs.getString("value")) + 1;
                ps.setString(1,  "" + instanceNr);
                ps.executeUpdate();
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    ////////////////////////////////////////
    // Channels
    ////////////////////////////////////////
    public static void insertChannel(String command, String channel) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO channels (command, channel) VALUES (?, ?)"
            );
            ps.setString(1, command);
            ps.setString(2, channel);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removeChannel(String command, String channel) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM channels WHERE command = ? AND channel = ?"
            );
            ps.setString(1, command);
            ps.setString(2, channel);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static HashMap<String, ArrayList<String>> getChannels() {
        HashMap<String, ArrayList<String>> channels = new HashMap<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM channels"
            );
            ResultSet rs = ps.executeQuery();
            String command;
            while (!rs.isClosed() && rs.next()) {
                if (channels.containsKey(command = rs.getString("command"))) {
                    channels.get(command).add(rs.getString("channel"));
                } else {
                    channels.put(command, new ArrayList<>(List.of(rs.getString("channel"))));
                }
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return channels;
    }

    ////////////////////////////////////////
    // servers
    ////////////////////////////////////////
    public static void insertServer(String server) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO servers (server) VALUES (?)"
            );
            ps.setString(1, server);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removeServer(String server) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM channels WHERE channel = ?"
            );
            ps.setString(1, server);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static ArrayList<String> getServers() {
        ArrayList<String> servers = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM servers"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                servers.add(rs.getString("server"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return servers;
    }

    ////////////////////////////////////////
    // userBlacklist
    ////////////////////////////////////////
    public static void insertBlacklist(String user) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO userBlacklist (user) VALUES (?)"
            );
            ps.setString(1, user);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removeBlacklist(String user) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM userBlacklist WHERE user = ?"
            );
            ps.setString(1, user);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static ArrayList<String> getBlacklist() {
        ArrayList<String> users = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM userBlacklist"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                users.add(rs.getString("user"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return users;
    }

    ////////////////////////////////////////
    // Place Queue
    ////////////////////////////////////////
    public static void insertPlaceQ(int key, String file, String user) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO place_queue (key, file, user) VALUES (?, ?, ?)"
            );
            ps.setInt(1, key);
            ps.setString(2, file);
            ps.setString(3, user);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void updatePlaceQ(int key, int progress) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE place_queue SET progress = ? WHERE key = ?"
            );
            ps.setInt(1, progress);
            ps.setInt(2, key);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removePlaceQ(int key) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM place_queue WHERE key = ?"
            );
            ps.setInt(1, key);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static String[] getPlaceQProject(int key) {
        String[] project = new String[3];
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM place_queue WHERE key = ?"
            );
            ps.setInt(1, key);
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                project[0] = rs.getString("file");
                project[1] = rs.getString("progress");
                project[2] = rs.getString("user");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return project;
    }

    public static String[] getCompletePlaceQ() {
        String[] strs = new String[]{"", "", ""};
        String project = "";
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM place_queue"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                strs[0] += rs.getInt("key") + "\n";
                strs[1] += rs.getInt("progress") + "\n";
                strs[2] += "<@!" + rs.getString("user") + ">\n";
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return strs;
    }

    public static ArrayList<Integer> getPlaceQIDs() {
        ArrayList<Integer> ids = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM place_queue"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                ids.add(rs.getInt("key"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return ids;
    }

    ////////////////////////////////////////
    // SpokesPeople
    ////////////////////////////////////////
//    public static void insertSpokesPeople(ArrayList<String> spokesPeople) {
//        try (Connection connection = ConnectionPool.getConnection()){
//            PreparedStatement ps = connection.prepareStatement(
//                    "INSERT INTO spokesPeople (user, subject, year) VALUES (?, ?, ?)"
//            );
//            for (String s : spokesPeople) {
//                String[] temp = s.split("_");
//                ps.setString(1, temp[0]);
//                ps.setString(2, temp[1]);
//                ps.setString(3, temp[2]);
//                ps.addBatch();
//            }
//            ps.executeBatch();
//        } catch (SQLException sqlE) {
//            LOGGER.error("SQL Exception", sqlE);
//        }
//    }
//
//    public static void removeSpokesPeople(ArrayList<String> spokesPeople) {
//        try (Connection connection = ConnectionPool.getConnection()){
//            PreparedStatement ps = connection.prepareStatement(
//                    "DELETE FROM spokesPeople WHERE user = ?"
//            );
//            for (String user : spokesPeople) {
//                ps.setString(1, user);
//                ps.addBatch();
//            }
//            ps.executeBatch();
//        } catch (SQLException sqlE) {
//            LOGGER.error("SQL Exception", sqlE);
//        }
//    }

    public static ArrayList<HashMap<String, String>> getSpokesPeople() {
        ArrayList<HashMap<String, String>> spokesPeople = new ArrayList<>();
        spokesPeople.add(new HashMap<>());
        spokesPeople.add(new HashMap<>());
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM spokesPeople"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                if (rs.getInt("year") == 1) {
                    spokesPeople.get(0).put(rs.getString("user"), rs.getString("subject"));
                } else {
                    spokesPeople.get(1).put(rs.getString("user"), rs.getString("subject"));
                }
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return spokesPeople;
    }

    ////////////////////////////////////////
    // Message Delete
    ////////////////////////////////////////
    private static int instanceNr;

    public static void insertDeleteMessage(String server, String channel, String id, long time) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO deleteMsgs (server, channel, id, deleteTime, instanceNr) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setString(1, server);
            ps.setString(2, channel);
            ps.setString(3, id);
            ps.setLong(4, time);
            ps.setInt(5, instanceNr);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static ArrayList<ArrayList<String>> getDeleteMessages() {
        ArrayList<ArrayList<String>> msgs = new ArrayList<>();
        msgs.add(new ArrayList<>());
        msgs.add(new ArrayList<>());
        msgs.add(new ArrayList<>());
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM deleteMsgs"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                if (rs.getInt("instanceNr") < instanceNr) {
                    msgs.get(0).add(rs.getString("server"));
                    msgs.get(1).add(rs.getString("channel"));
                    msgs.get(2).add(rs.getString("id"));
                }
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return msgs;
    }

    public static void pruneTableDeleteMsgs() {
        long currentTime = System.currentTimeMillis();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM deleteMsgs WHERE deleteTime<?"
            );
            ps.setLong(1, currentTime);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }
    ////////////////////////////////////////
    // ExportDatabase
    ////////////////////////////////////////
    public static ArrayList<String> getTables() {
        ArrayList<String> tables = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            DatabaseMetaData dbmd = connection.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, null, null);
            while(!rs.isClosed() && rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return tables;
    }

    public static ArrayList<String> getColumns(String tableName) {
        ArrayList<String> tables = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            ResultSet rs = dbmd.getColumns(null, null, tableName, null);
            while (!rs.isClosed() && rs.next()) {
                tables.add(rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return tables;
    }

    public static ArrayList<String> getValuesOfTable(String tableName, ArrayList<String> columns) {
        ArrayList<String> data = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            String query = "SELECT * FROM " + tableName;

            ResultSet rs = connection.createStatement().executeQuery(query);
            while (!rs.isClosed() && rs.next()) {
                StringBuilder sb = new StringBuilder();
                for (String column : columns) {
                    sb.append(rs.getString(column)).append("\t");
                }
                data.add(sb.toString());
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return data;
    }

    ////////////////////////////////////////
    // Database integrity
    ////////////////////////////////////////
    public static boolean createTableIfNotExists(String tableName, String arguments) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + "(\n" +
                    arguments + ");");
            return ps.execute();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return false;
    }

    ////////////////////////////////////////
    // SQL
    ////////////////////////////////////////
    public static int sqlExecuteUpdate(String command) {
        try (Connection connection = ConnectionPool.getConnection()){
            return connection.createStatement().executeUpdate(command);
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return -1;
    }

    ////////////////////////////////////////
    // Course
    ////////////////////////////////////////
    public static String getCourse(String courseNumber) {
        String name = "";
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM courses WHERE courseNumber=?"
            );
            ps.setString(1, courseNumber);
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                name = rs.getString("courseNumber") + " - " + rs.getString("courseName");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return name;
    }

    public static ArrayList<String> getCourseReview(String courseNumber) {
        ArrayList<String> reviews = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM courseReviews WHERE courseNumber=? AND verified=1"
            );
            ps.setString(1, courseNumber);
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                reviews.add(rs.getString("review"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return reviews;
    }

    ////////////////////////////////////////
    // CourseReview
    ////////////////////////////////////////
    public static void insertCourseReview(String id, String review, String courseNumber) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO courseReviews (userId, review, courseNumber, date) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, id);
            ps.setString(2, review);
            ps.setString(3, courseNumber);
            ps.setLong(4, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static int containsCourseNumber(String courseNumber) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT EXISTS(SELECT 1 FROM courses WHERE courseNumber=?) AS containsCheck"
            );
            ps.setString(1, courseNumber);
            return ps.executeQuery().getInt("containsCheck");
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return -1;
    }

    public static void insertCourse(String courseNumber) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO courses (courseNumber, courseName) VALUES (?, ?)"
            );
            ps.setString(1, courseNumber);
            ps.setString(2, "???");
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    ////////////////////////////////////////
    // CourseReviewVerify
    ////////////////////////////////////////
    public static HashMap<Integer, String[]> getUnverifiedReviews() {
        HashMap<Integer, String[]> reviews = new HashMap<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM courseReviews WHERE verified=0"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                reviews.put(rs.getInt("key"), new String[]{rs.getString("review"), rs.getString("userId")});
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return reviews;
    }

    public static void updateVerifiedStatus(int key, int value) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE courseReviews SET verified = ? WHERE key = ?"
            );
            ps.setInt(1, value);
            ps.setInt(2, key);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }
}
