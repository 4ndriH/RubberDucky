package services.onStartup;

import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.PermissionManager;
import services.database.DatabaseHandler;

import java.io.File;
import java.util.*;

public class OnStartUp {
    private final Logger LOGGER = LoggerFactory.getLogger(OnStartUp.class);

    public OnStartUp() {
        DirectoryVerification.verifyDirectoryIntegrity(LOGGER);
        DirectoryVerification.verifyFileIntegrity(LOGGER);
        DatabaseVerification.verifyDatabaseIntegrity(LOGGER);
        DatabaseVerification.databaseValueImport(LOGGER);
        DatabaseHandler.incrementStartUpCounter();
        updateToken();
        CONFIG.reload();
        PermissionManager.reload();
        MessageCleanUp();
    }

    private void MessageCleanUp () {
        (new Thread(() -> {
            while(CONFIG.instance == null) {
                try {
                    Thread.sleep(32768);
                } catch (InterruptedException ignored) {}
            }

            ArrayList<ArrayList<String>> msgs = DatabaseHandler.getDeleteMessages();
            for (int i = 0; i < msgs.get(0).size(); i++) {
                try {
                    CONFIG.instance.getGuildById(msgs.get(0).get(i))
                            .getTextChannelById(msgs.get(1).get(i))
                            .deleteMessageById(msgs.get(2).get(i)).queue(
                                    null, new ErrorHandler()
                                            .ignore(ErrorResponse.UNKNOWN_MESSAGE, ErrorResponse.MISSING_PERMISSIONS)
                            );
                    Thread.sleep(1024);
                } catch (Exception ignored) {}
            }
        })).start();
    }

    private void updateToken() {
        Scanner scanner;
        File txt = new File("token.txt");
        try {
            scanner = new Scanner(txt);
        } catch (Exception e) {
            return;
        }

        DatabaseHandler.updateConfig("token", scanner.next());
        txt.deleteOnExit();
        LOGGER.warn("The token has been updated!");
        scanner.close();
    }
}
