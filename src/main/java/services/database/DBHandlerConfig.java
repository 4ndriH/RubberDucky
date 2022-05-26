package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DBHandlerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerConfig.class);

    public static void updateConfig(String key, String value) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE Config SET Value = ? WHERE Key = ?"
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
                    "SELECT * FROM Config"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                config.put(rs.getString("Key"), rs.getString("Value"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return config;
    }

    public static void incrementUptimeCounter() {
        updateConfig("UptimeCount", "" + (Integer.parseInt(getConfig().get("systemStartUps")) + 1));
    }
}
