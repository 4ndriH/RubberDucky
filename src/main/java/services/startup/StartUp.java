package services.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Config;
import services.PermissionManager;
import services.database.DBHandlerConfig;

public class StartUp {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartUp.class);

    public static void actions() {
        verifications();
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
        Config.initConfig();
        PermissionManager.reload();
        LOGGER.info("Permissions loaded");
    }
}
