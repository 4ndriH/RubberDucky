package services.database;

import java.sql.*;
import java.util.*;

public class dbHandlerQ {
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
            connection = DriverManager.getConnection("jdbc:sqlite:DB/place.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static void addToQ(int id, String file, String user) {
        try {
            PreparedStatement statement = connectToDB().prepareStatement(
                    "INSERT INTO queue (key, file, user) VALUES (? , ?, ?)"
            );
            statement.setInt(1, id);
            statement.setString(2, file);
            statement.setString(3, user);
            statement.executeUpdate();
            statement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void updateProgressInQ(int progress, int id) {
        try {
            PreparedStatement updateStatement = connectToDB().prepareStatement(
                    "UPDATE queue SET progress = ? WHERE key = ?"
            );
            updateStatement.setInt(1, progress);
            updateStatement.setInt(2, id);
            updateStatement.executeUpdate();
            updateStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static int getProgress(int id) {
        int progress = 0;
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM queue WHERE key = ?"
            );
            getStatement.setInt(1, id);
            ResultSet rs = getStatement.executeQuery();
            progress = rs.getInt("progress");
            getStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return progress;
    }

    public static void deleteElementInQ(int id) {
        try {
            PreparedStatement deleteStatement = connectToDB().prepareStatement(
                    "DELETE FROM queue WHERE key = ?"
            );
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
            deleteStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static ArrayList<Integer> getIDs() {
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM queue"
            );
            ResultSet rs = getStatement.executeQuery();
            while (!rs.isClosed() && rs.next())
                ids.add(rs.getInt("key"));
            getStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ids;
    }

    public static String getFile(int id) {
        String file = "";
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM queue WHERE key = ?"
            );
            getStatement.setInt(1, id);
            ResultSet rs = getStatement.executeQuery();
            if (!rs.isClosed())
                file = rs.getString("file");
            getStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return file;
    }

    public static ResultSet getAll() {
        ResultSet rs = null;
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM queue"
            );
            rs = getStatement.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public static String getUser(int id) {
        String user = "";
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM queue WHERE key = ?"
            );
            getStatement.setInt(1, id);
            ResultSet rs = getStatement.executeQuery();
            if (!rs.isClosed())
                user = rs.getString("user");
            getStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }
}
