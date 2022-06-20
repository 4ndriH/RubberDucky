package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHandlerSnowflakePermissions {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerWhitelistedChannels.class);

    public static void addSnowflakePermissions(String discordUserid, String discordServerId, String discordChannelId, String command) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO SnowflakePermissions VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, discordUserid);
            ps.setString(2, discordServerId);
            ps.setString(3, discordChannelId);
            ps.setString(4, command);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removeSnowflakePermissions(String discordUserid, String discordServerId, String discordChannelId, String command) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM SnowflakePermissions WHERE DiscordUserId=? AND DiscordServerId=? AND DiscordChannelId = ? AND Command = ?"
            );
            ps.setString(1, discordUserid);
            ps.setString(2, discordServerId);
            ps.setString(3, discordChannelId);
            ps.setString(4, command);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> getSnowflakePermissions() {
        HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> snowflakes = new HashMap<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM SnowflakePermissions"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                String discordUserId = rs.getString("DiscordUserId");
                String discordServerId = rs.getString("DiscordServerId");
                String discordChannelId = rs.getString ("DiscordChannelId");
                if (!snowflakes.containsKey(discordUserId)) {
                    snowflakes.put(discordUserId, new HashMap<>());
                }
                if (!snowflakes.get(discordUserId).containsKey(discordServerId)) {
                    snowflakes.get(discordUserId).put(discordServerId, new HashMap<>());
                }
                if (!snowflakes.get(discordUserId).get(discordServerId).containsKey(discordChannelId)) {
                    snowflakes.get(discordUserId).get(discordServerId).put(discordChannelId, new ArrayList<>());
                }
                snowflakes.get(discordUserId).get(discordServerId).get(discordChannelId).add(rs.getString("Command"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return snowflakes;
    }
}
