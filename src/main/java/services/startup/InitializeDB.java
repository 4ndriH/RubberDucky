package services.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.daos.ConfigDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class InitializeDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitializeDB.class);

    private static class ConfigTriple {
        private String key;
        private String value;
        private String type;

        public ConfigTriple(String key, String value, String type) {
            this.key = key;
            this.value = value;
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }
    }

    private static final ArrayList<ConfigTriple> configValues = new ArrayList<>() {{
        add(new ConfigTriple("token", readToken(), "STRING"));
        add(new ConfigTriple("prefix", "drd", "STRING"));
        add(new ConfigTriple("ownerId", "0", "STRING"));
        add(new ConfigTriple("logChannel", "865693419376738315", "STRING"));
        add(new ConfigTriple("embedColor", "0xb074ad", "STRING"));
        add(new ConfigTriple("placeProject", "-1", "INTEGER"));
        add(new ConfigTriple("ButtonScore", "0", "INTEGER"));
        add(new ConfigTriple("PlaceVerify", "false", "BOOLEAN"));
    }};

    public static void loadBasicConfigValues() {
        ConfigDAO configDAO = new ConfigDAO();
        int valuesAdded = 0;

        for (ConfigTriple cfgTriple : configValues) {
            boolean added = configDAO.addConfigEntryIfNotExists(cfgTriple.getKey(), cfgTriple.getValue(), cfgTriple.getType());

            if (added) {
                LOGGER.info("Added {} to the Config table", cfgTriple.getKey());
            }
        }

        if (configDAO.getConfigEntry("token").getValue().isEmpty()) {
            LOGGER.error("Missing token");
            System.exit(1);
        }
    }

    private static String readToken() {
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
