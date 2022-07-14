package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHandlerPingHell {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerPingHell.class);

    public static void addPingHellMember(String discordUserId) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO PinghellHQ (DiscordUserId) VALUES (?)"
            );
            ps.setString(1, discordUserId);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static boolean isInPinghellHQ(String discordUserId) {
        int size = 0;
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM PinghellHQ WHERE DiscordUserId=?"
            );
            ps.setString(1, discordUserId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isClosed() && rs.next()) {
                size++;
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return size == 1;
    }

    public static boolean isInPinghell(String discordUserId) {
        int status = 0;
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT PinghellStatus FROM PinghellHQ WHERE DiscordUserId=?"
            );
            ps.setString(1, discordUserId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isClosed() && rs.next()) {
                status = rs.getInt("PinghellStatus");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return status == 1;
    }

    public static boolean isServerMember(String discordUserId) {
        int status = 0;
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT ServerMember FROM PinghellHQ WHERE DiscordUserId=?"
            );
            ps.setString(1, discordUserId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isClosed() && rs.next()) {
                status = rs.getInt("ServerMember");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return status == 1;
    }

    public static void updatePinghellStatus(String discordUserId, int status) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE PinghellHQ SET PinghellStatus=? WHERE DiscordUserId=?"
            );
            ps.setInt(1, status);
            ps.setString(2, discordUserId);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void updateServerMemberStatus(String discordUserId, int status) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE PinghellHQ SET ServerMember=? WHERE DiscordUserId=?"
            );
            ps.setInt(1, status);
            ps.setString(2, discordUserId);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }
 }
