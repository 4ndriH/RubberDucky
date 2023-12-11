package assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerConfig;

import java.awt.*;
import java.util.HashMap;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger("Config");

    public static String token;
    public static String prefix;
    public static String ownerID;
    public static String logChannelID;
    public static Color embedColor;
    public static boolean placeVerify;

    public static void reload() {
        HashMap<String, String> config = DBHandlerConfig.getConfig();
        token = config.get("token");
        prefix = config.get("prefix");
        ownerID = config.get("ownerId");
        logChannelID = config.get("logChannel");
        embedColor = Color.decode(config.get("embedColor"));
        placeVerify = Boolean.parseBoolean(config.get("PlaceVerify"));

        LOGGER.warn("Config loaded");
    }
}