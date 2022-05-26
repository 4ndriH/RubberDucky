package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHandlerWhitelistedChannels {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerWhitelistedChannels.class);

    public static void addChannelToWhitelist(String discordChannelId, String command) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO WhitelistedChannels (DiscordChannelId, Command) VALUES (?, ?)"
            );
            ps.setString(1, discordChannelId);
            ps.setString(2, command);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removeChannelFromWhitelist(String discordChannelId, String command) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM WhitelistedChannels WHERE DiscordChannelId = ? AND command = ?"
            );
            ps.setString(1, discordChannelId);
            ps.setString(2, command);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static HashMap<String, ArrayList<String>> getWhitelistedChannels() {
        HashMap<String, ArrayList<String>> whitelistedChannels = new HashMap<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM WhitelistedChannels"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                String command = rs.getString("Command");
                if (!whitelistedChannels.containsKey(command)) {
                    whitelistedChannels.put(command, new ArrayList<>());
                }
                whitelistedChannels.get(command).add(rs.getString("DiscordChannelId"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return whitelistedChannels;
    }
}
