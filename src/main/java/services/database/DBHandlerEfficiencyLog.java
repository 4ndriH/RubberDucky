package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
