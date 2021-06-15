package services.database;

import java.sql.*;
import java.util.HashMap;

public class dbHandlerConfig {
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
            connection = DriverManager.getConnection("jdbc:sqlite:DB/config.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static void updateConfig(String key, String value) {
        try {
            PreparedStatement updateStatement = connectToDB().prepareStatement(
                    "UPDATE config SET value = ? WHERE key = ?"
            );
            updateStatement.setString(1, value);
            updateStatement.setString(2, key);
            updateStatement.executeUpdate();
            updateStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static HashMap<String, String> getConfig() {
        HashMap<String, String> config = new HashMap<>();
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM config"
            );
            ResultSet rs = getStatement.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                config.put(rs.getString("key"), rs.getString("value"));
            }
            getStatement.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return config;
    }
}
