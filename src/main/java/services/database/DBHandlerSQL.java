package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DBHandlerSQL {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerSQL.class);

    public static int sqlExecuteUpdate(String command) {
        try (Connection connection = ConnectionPool.getConnection()){
            return connection.createStatement().executeUpdate(command);
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return -1;
    }
}
