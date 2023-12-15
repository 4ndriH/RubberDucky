package services.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerStartUp;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class InitializeDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitializeDB.class);

    private static final LinkedHashMap<String, String> dbValues = new LinkedHashMap<>() {{
        put("token", updateToken());
        put("prefix", "drd");
        put("ownerId", "0");
        put("logChannel", "865693419376738315");
        put("systemStartUps", "0");
        put("embedColor", "0xb074ad");
        put("placeProject", "-1");
        put("ButtonScore", "0");
        put("PlaceVerify", "false");
    }};

    public static void loadBasicConfigValues() {
        int valuesAdded = 0;

        for (String key : dbValues.keySet()) {
            boolean ret = DBHandlerStartUp.addConfigEntryIfNotExists(key, dbValues.get(key));

            if (ret) {
                valuesAdded++;
            } else if (key.equals("token") && !dbValues.get(key).isEmpty()) {
                DBHandlerStartUp.updateToken(dbValues.get(key));
                LOGGER.info("Token has been updated");
            }

            if (DBHandlerStartUp.getToken().isEmpty()) {
                LOGGER.error("Missing token");
                System.exit(1);
            }
        }

        if (valuesAdded != 0) {
            LOGGER.info("Added " + valuesAdded + " values to the Config table");
        }
    }

    private static String updateToken() {
        File txt = new File("token.txt");
        Scanner scanner;
        String token;

        try {
            scanner = new Scanner(txt);
            token = scanner.next();
        } catch (Exception e) {
            return "";
        }

        txt.deleteOnExit();
        return token;
    }
}
