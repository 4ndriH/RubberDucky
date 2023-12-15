package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHandlerStartUp {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerStartUp.class);

    public static boolean doesTableExist(String tableName) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT name FROM sqlite_master WHERE tbl_name=?"
            );
            ps.setString(1, tableName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return true;
    }

    public static void createTableIfNotExists(String tableName, String arguments) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + tableName + "(\n" + arguments + ")"
            );
            ps.execute();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static boolean addConfigEntryIfNotExists(String key, String value) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Config (Key, Value) VALUES (?, ?)"
            );
            ps.setString(1, key);
            ps.setString(2, value);
            ps.execute();
        } catch (SQLException sqlE) {
            if (!sqlE.getMessage().contains("A PRIMARY KEY constraint failed")) {
                LOGGER.error("SQL Exception", sqlE);
            }
            return false;
        }
        return true;
    }

    public static String getToken() {
        String token = "";
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT Value FROM Config WHERE Key='token'"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                token = rs.getString("Value");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return token;
    }

    public static void updateToken(String token) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE Config SET Value=? WHERE Key='token'"
            );
            ps.setString(1, token);
            ps.execute();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }
}
