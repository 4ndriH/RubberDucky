package assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerConfig;

import java.awt.*;
import java.util.HashMap;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger("Config");

    public static String directoryPath = System.getenv("DATA_DIRECTORY") == null ? "" : System.getenv("DATA_DIRECTORY") + "/";
    public static String token;
    public static String prefix;
    public static String ownerID;
    public static String logChannelID;
    public static Color embedColor;
    public static boolean placeVerify;

    public static void updateConfig(String key, String value) {
        DBHandlerConfig.updateConfig(key, value);

        switch (key) {
            case "token" -> token = value;
            case "prefix" -> prefix = value;
            case "ownerId" -> ownerID = value;
            case "logChannel" -> logChannelID = value;
            case "embedColor" -> embedColor = Color.decode(value);
            case "PlaceVerify" -> placeVerify = Boolean.parseBoolean(value);
        }

        LOGGER.info("Config updated: " + key.toUpperCase() + " change to \"" + value + "\"");
    }

    public static void initConfig() {
        HashMap<String, String> config = DBHandlerConfig.getConfig();
        token = config.get("token");
        prefix = config.get("prefix");
        ownerID = config.get("ownerId");
        logChannelID = config.get("logChannel");
        embedColor = Color.decode(config.get("embedColor"));
        placeVerify = Boolean.parseBoolean(config.get("PlaceVerify"));
        LOGGER.info("Config initialized");
    }
}
