package services.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class dbHandlerPermissions {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Connection connectToDB () {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:DB/permissions.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    ////////////////////////////////////////////////
    // BlackList
    ////////////////////////////////////////////////

    public static ArrayList<String> getBlacklist() {
        ArrayList<String> ids = new ArrayList<>();
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM userblacklist"
            );
            ResultSet rs = getStatement.executeQuery();
            while (!rs.isClosed() && rs.next())
                ids.add(rs.getString("user"));
            getStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ids;
    }

    public static void removeFromBlackList(String id) {
        try {
            PreparedStatement deleteStatement = connectToDB().prepareStatement(
                    "DELETE FROM userblacklist WHERE user = ?"
            );
            deleteStatement.setString(1, id);
            deleteStatement.executeUpdate();
            deleteStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void addToBlackList(String id) {
        try {
            PreparedStatement insertStatement = connectToDB().prepareStatement(
                    "INSERT INTO userblacklist (user) VALUES (?)"
            );
            insertStatement.setString(1, id);
            insertStatement.executeUpdate();
            insertStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static ArrayList<String> getServers() {
        ArrayList<String> ids = new ArrayList<>();
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM servers"
            );
            ResultSet rs = getStatement.executeQuery();
            while (!rs.isClosed() && rs.next())
                ids.add(rs.getString("server"));
            getStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ids;
    }

    ////////////////////////////////////////////////
    // Servers
    ////////////////////////////////////////////////

    public static void removeFromServers(String id) {
        try {
            PreparedStatement deleteStatement = connectToDB().prepareStatement(
                    "DELETE FROM servers WHERE server = ?"
            );
            deleteStatement.setString(1, id);
            deleteStatement.executeUpdate();
            deleteStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void addToServers(String id) {
        try {
            PreparedStatement insertStatement = connectToDB().prepareStatement(
                    "INSERT INTO servers (server) VALUES (?)"
            );
            insertStatement.setString(1, id);
            insertStatement.executeUpdate();
            insertStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    ////////////////////////////////////////////////
    // Channels
    ////////////////////////////////////////////////

    public static HashMap<String, ArrayList<String>> getChannels() {
        HashMap<String, ArrayList<String>> channels = new HashMap<>();
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM channels"
            );
            ResultSet rs = getStatement.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                String command = rs.getString("command");
                String channel = rs.getString("channel");

                if (channels.containsKey(command)) {
                    channels.get(command).add(channel);
                } else {
                    channels.put(command, new ArrayList<>(List.of(channel)));
                }
            }
            getStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return channels;
    }

    public static void removeFromChannels(String command, String channel) {
        try {
            PreparedStatement deleteStatement = connectToDB().prepareStatement(
                    "DELETE FROM channels WHERE command = ? AND channel = ?"
            );
            deleteStatement.setString(1, command);
            deleteStatement.setString(2, channel);
            deleteStatement.executeUpdate();
            deleteStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void addToChannels(String command, String channel) {
        try {
            PreparedStatement insertStatement = connectToDB().prepareStatement(
                    "INSERT INTO channels (command, channel) VALUES (?, ?)"
            );
            insertStatement.setString(1, command);
            insertStatement.setString(2, channel);
            insertStatement.executeUpdate();
            insertStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
