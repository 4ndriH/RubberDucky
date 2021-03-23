package services;

import java.sql.*;
import java.util.*;

public class dbHandler {
    private static Connection connectToDB () {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:DB/RubberDucky.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static void addToQ (int id, String file) {
        try {
            PreparedStatement statement = connectToDB().prepareStatement(
                    "insert into queue (key, file) values (? , ?)"
            );
            statement.setInt(1, id);
            statement.setString(1, file);
            statement.setQueryTimeout(30);
            int addedRow = statement.executeUpdate();
            System.out.println("Added " + addedRow + " users");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void updateProgressInQ (int progress, int id) {
        try {
            PreparedStatement updateStatement = connectToDB().prepareStatement(
                    "update queue set progress = ? where key = ?"
            );
            updateStatement.setInt(1, progress);
            updateStatement.setInt(2, id);
            int updatedRow = updateStatement.executeUpdate();
            System.out.println("Updated " + updatedRow + " users");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static int getProgress (int id) {
        int progress = 0;
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "select * from queue where key = ?"
            );
            getStatement.setInt(1, id);
            ResultSet rs = getStatement.executeQuery();
            id = rs.getInt("progress");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return progress;
    }

    public static void deleteElementInQ (int id) {
        try {
            PreparedStatement deleteStatement = connectToDB().prepareStatement(
                    "delete from queue where key > ?"
            );
            deleteStatement.setInt(1, id);
            int deletedRows = deleteStatement.executeUpdate();
            System.out.println("Deleted " + deletedRows + " users");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static ArrayList<Integer> getIDs () {
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            ResultSet rs = connectToDB().createStatement().executeQuery("select * from queue");
            while (rs.next())
                ids.add(rs.getInt("key"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ids;
    }

    public static String getByID (int id) {
        String file = "";
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "select * from queue where key = ?"
            );
            getStatement.setInt(1, id);
            ResultSet rs = getStatement.executeQuery();
            file = rs.getString("file");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return file;
    }
}
