package services.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        config.setJdbcUrl( "jdbc:sqlite:DB/RubberDucky.db" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.addDataSourceProperty("foreign_keys", "true");
        dataSource = new HikariDataSource( config );
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException sqlE) {
            LOGGER.error("Could not establish connection to the database", sqlE);
            return null;
        }
    }

    public static void closeDBConnection() {
        dataSource.close();
    }
}
