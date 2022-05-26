package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBHandlerBlacklistedUsers {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerBlacklistedUsers.class);

    public static void addUserToBlacklist(String discordUserId) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO BlacklistedUsers (DiscordUserId) VALUES (?)"
            );
            ps.setString(1, discordUserId);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removeUserFromBlacklist(String discordUserId) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM BlacklistedUsers WHERE DiscordUserId = ?"
            );
            ps.setString(1, discordUserId);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static ArrayList<String> getBlacklistedUsers() {
        ArrayList<String> blacklistedServers = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM BlacklistedUsers"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                blacklistedServers.add(rs.getString("DiscordUserId"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return blacklistedServers;
    }
}
