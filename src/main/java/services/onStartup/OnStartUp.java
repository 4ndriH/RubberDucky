package services.onStartup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.API.CourseReview;
import services.PermissionManager;
import services.database.DBHandlerConfig;

import java.io.File;
import java.util.Scanner;

public class OnStartUp {
    private final Logger LOGGER = LoggerFactory.getLogger(OnStartUp.class);

    public OnStartUp() {
        DirectoryVerification.verifyDirectoryIntegrity(LOGGER);
        DirectoryVerification.verifyFileIntegrity(LOGGER);
        //DatabaseVerification.verifyDatabaseIntegrity(LOGGER);
        //DatabaseVerification.databaseValueImport(LOGGER);
        DBHandlerConfig.incrementUptimeCounter();
        updateToken();
        CONFIG.reload();
        PermissionManager.reload();

        CourseReview.api();
    }

    private void updateToken() {
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
