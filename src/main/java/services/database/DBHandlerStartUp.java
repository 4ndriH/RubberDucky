package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHandlerStartUp {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerStartUp.class);

    public static boolean doesTableExist(String tableName) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT name FROM sqlite_master WHERE tbl_name=?"
            );
            ps.setString(1, tableName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return true;
    }

    public static void createTableIfNotExists(String tableName, String arguments) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + tableName + "(\n" + arguments + ")"
            );
            ps.execute();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }
}
