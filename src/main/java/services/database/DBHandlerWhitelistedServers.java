package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBHandlerWhitelistedServers {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerWhitelistedServers.class);

    public static void addServerToWhitelist(String discordServerId) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO WhitelistedServers (DiscordServerId) VALUES (?)"
            );
            ps.setString(1, discordServerId);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removeServerFromWhitelist(String discordServerId) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM WhitelistedServers WHERE DiscordServerId = ?"
            );
            ps.setString(1, discordServerId);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static ArrayList<String> getWhitelistedServers() {
        ArrayList<String> whitelistedServers = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM WhitelistedServers"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                whitelistedServers.add(rs.getString("DiscordServerId"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return whitelistedServers;
    }
}
