package services.onStartup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.CONFIG;
import services.PermissionManager;
import services.database.ConnectionPool;
import services.database.DBHandlerConfig;

import java.io.File;
import java.util.Scanner;

public class StartUp {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartUp.class);

    public static void actions() {
        verifications();
        new ConnectionPool();
        updateToken();
        loadEssentials();
        DBHandlerConfig.incrementUptimeCounter();
    }

    private static void verifications() {
        DirectoryVerification.verifyDirectoryIntegrity(LOGGER);
        //DirectoryVerification.verifyFileIntegrity(LOGGER);
        DatabaseVerification.verifyDatabaseIntegrity(LOGGER);
    }

    private static void loadEssentials() {
        CONFIG.reload();
        PermissionManager.reload();
    }

    private static void updateToken() {
        Scanner scanner;
        File txt = new File("token.txt");
        try {
            scanner = new Scanner(txt);
        } catch (Exception e) {
            return;
        }

        DBHandlerConfig.updateConfig("token", scanner.next());
        txt.deleteOnExit();
        LOGGER.warn("The token has been updated!");
        scanner.close();
    }
}
