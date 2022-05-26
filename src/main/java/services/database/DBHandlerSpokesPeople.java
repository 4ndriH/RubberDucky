package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHandlerSpokesPeople {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerSpokesPeople.class);

    public static ArrayList<HashMap<String, String>> getSpokesPeople() {
        ArrayList<HashMap<String, String>> spokesPeople = new ArrayList<>();
        spokesPeople.add(new HashMap<>());
        spokesPeople.add(new HashMap<>());
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM SpokesPeople"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                if (rs.getInt("year") == 1) {
                    spokesPeople.get(0).put(rs.getString("DiscordUserId"), rs.getString("Subject"));
                } else {
                    spokesPeople.get(1).put(rs.getString("DiscordUserId"), rs.getString("Subject"));
                }
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return spokesPeople;
    }
}
