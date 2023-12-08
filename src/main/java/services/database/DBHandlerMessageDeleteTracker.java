package services.database;

import assets.Objects.DeletableMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHandlerMessageDeleteTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerMessageDeleteTracker.class);

    public static void insertDeleteMessage(String discordServerId, String discordChannelId, String discordMessageId, long deleteTime) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO MessageDeleteTracker (DiscordServerId, DiscordChannelId, DiscordMessageId, DeleteTime, UptimeNumber) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setString(1, discordServerId);
            ps.setString(2, discordChannelId);
            ps.setString(3, discordMessageId);
            ps.setLong(4, deleteTime);
            ps.setInt(5, Integer.parseInt(DBHandlerConfig.getConfig().get("systemStartUps")));
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void pruneMessageDeleteTracker(long currentSystemTime) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM MessageDeleteTracker WHERE DeleteTime<?"
            );
            ps.setLong(1, currentSystemTime);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static ArrayList<DeletableMessage> getMessagesToDelete() {
        int SystemStartUps = Integer.parseInt(DBHandlerConfig.getConfig().get("systemStartUps"));
        ArrayList<DeletableMessage> messages = new ArrayList<>();

        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM MessageDeleteTracker WHERE UptimeNumber < ?"
            );
            ps.setInt(1, SystemStartUps);
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                String DiscordServerId = rs.getString("DiscordServerId");
                String DiscordChannelId = rs.getString("DiscordChannelId");
                String DiscordMessageId = rs.getString("DiscordMessageId");
                long DeleteTime = rs.getLong("DeleteTime");
                messages.add(new DeletableMessage(DiscordServerId, DiscordChannelId, DiscordMessageId, DeleteTime));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }

        pruneMessageDeleteTracker(System.currentTimeMillis());

        return messages;
    }
}
