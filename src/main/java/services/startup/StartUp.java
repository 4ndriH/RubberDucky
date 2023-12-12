package services.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Config;
import services.PermissionManager;
import services.database.ConnectionPool;
import services.database.DBHandlerConfig;

public class StartUp {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartUp.class);

    public static void actions() {
        verifications();
        new ConnectionPool();
        InitializeDB.loadBasicConfigValues();
        loadEssentials();
        DBHandlerConfig.incrementUptimeCounter();
    }

    private static void verifications() {
        DirectoryVerification.verifyDirectoryIntegrity(LOGGER);
        DirectoryVerification.verifyFileIntegrity(LOGGER);
        DatabaseVerification.verifyDatabaseIntegrity(LOGGER);
    }

    private static void loadEssentials() {
        Config.reload();
        PermissionManager.reload();
    }
}
