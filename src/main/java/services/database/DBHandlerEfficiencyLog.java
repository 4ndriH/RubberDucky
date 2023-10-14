package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBHandlerEfficiencyLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerEfficiencyLog.class);

    public static void addDataPoint(int ethPlaceBots, int countThread) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO EfficiencyLog (PiT, EthPlaceBots, CountThread) VALUES (" + System.currentTimeMillis() + ", ?, ?)"
            );
            ps.setInt(1, ethPlaceBots);
            ps.setInt(2, countThread);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }
    public static ArrayList<Integer> getDataPoints(String channel) {
        ArrayList<Integer> dataPoints = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT " + (channel.equals("Count") ? "CountThread" : "EthPlaceBots") + " AS resCol FROM EfficiencyLog ORDER BY PiT desc LIMIT 1440"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                dataPoints.add(rs.getInt("resCol"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return dataPoints;
    }
}
