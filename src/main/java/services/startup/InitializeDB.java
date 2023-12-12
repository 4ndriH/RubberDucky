package services.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerStartUp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class InitializeDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitializeDB.class);

    private static HashMap<String, String> dbValues = new HashMap<>() {{
        put("token", updateToken());
        put("prefix", "drd");
        put("ownerId", "0");
        put("logChannel", "0");
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
            } else if (key.equals("token") && !dbValues.get(key).equals("")) {
                if (!DBHandlerStartUp.getToken().equals(dbValues.get(key))) {
                    DBHandlerStartUp.updateToken(dbValues.get(key));
                    LOGGER.info("Token has been updated");
                }
            }
        }

        if (valuesAdded != 0) {
            LOGGER.info("Added " + valuesAdded + " values to the Config table");
        }
    }

    private static String updateToken() {
        File txt = new File("resources/token.txt");
        Scanner scanner;
        String token;

        try {
            scanner = new Scanner(txt);
            token = scanner.next();
        } catch (FileNotFoundException fe) {
            LOGGER.error("Could not find token.txt");
            return "";
        } catch (Exception e) {
            return "";
        }
        return token;
    }
}
