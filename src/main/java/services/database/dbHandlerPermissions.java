package services.database;

import java.sql.*;
import java.util.ArrayList;

public class dbHandlerPermissions {
    private static Connection connectToDB () {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:DB/RubberDucky.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static boolean blackList (String id) {
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM userblacklist WHERE user = ?"
            );
            getStatement.setString(1, id);
            ResultSet rs = getStatement.executeQuery();
            if (!rs.isClosed()) {
                int status = rs.getInt("permission");
                return status == 1;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static boolean server (String id) {
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM servers WHERE server = ?"
            );
            getStatement.setString(1, id);
            ResultSet rs = getStatement.executeQuery();
            if (!rs.isClosed()) {
                int status = rs.getInt("status");
                return status == 1;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    public static ArrayList<String> roles (String command) {
        ArrayList<String> roles = new ArrayList<>();
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM roles WHERE command = ?"
            );
            getStatement.setString(1, command);
            ResultSet rs = getStatement.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                roles.add(rs.getString("role"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return roles;
    }

    public static ArrayList<String> channels (String command) {
        ArrayList<String> roles = new ArrayList<>();
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM channels WHERE channel = ?"
            );
            getStatement.setString(1, command);
            ResultSet rs = getStatement.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                roles.add(rs.getString("role"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return roles;
    }
}
