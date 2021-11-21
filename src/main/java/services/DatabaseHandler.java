package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public static void insertSpokesPeople(ArrayList<String> spokesPeople) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spokesPeople (user, subject, year) VALUES (?, ?, ?)"
            );
            for (String s : spokesPeople) {
                String[] temp = s.split("_");
                ps.setString(1, temp[0]);
                ps.setString(2, temp[1]);
                ps.setString(3, temp[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removeSpokesPeople(ArrayList<String> spokesPeople) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM spokesPeople WHERE user = ?"
            );
            for (String user : spokesPeople) {
                ps.setString(1, user);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

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
                    spokesPeople.get(1).put(rs.getString("user"), rs.getString("subject"));
                } else {
                    spokesPeople.get(2).put(rs.getString("user"), rs.getString("subject"));
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
    public static void insertDeleteMessage(String server, String channel, String id, long time) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO deleteMsgs (server, channel, id, deleteTime) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, server);
            ps.setString(2, channel);
            ps.setString(3, id);
            ps.setLong(4, time);
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
                try {
                    msgs.get(0).add(rs.getString("server"));
                    msgs.get(1).add(rs.getString("channel"));
                    msgs.get(2).add(rs.getString("id"));
                } catch (Exception sqlE) {
                    LOGGER.error("SQL Exception", sqlE);
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
}
