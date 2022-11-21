package services.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolCR {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    static {
        config.setJdbcUrl( "jdbc:sqlite:/usr/games/CRAPI/CourseReview.db" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.addDataSourceProperty("foreign_keys", "true");
        dataSource = new HikariDataSource( config );
    }

    //public ConnectionPoolCR() {}

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException sqlE) {
            sqlE.printStackTrace();
            return null;
        }
    }
}
