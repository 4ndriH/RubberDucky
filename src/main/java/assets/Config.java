package assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.daos.ConfigDAO;
import services.database.entities.ConfigORM;

import java.awt.*;
import java.util.List;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger("Config");

    public static String DIRECTORY_PATH = "bot_data/";
    public static String TOKEN;
    public static String PREFIX;
    public static String OWNER_ID;
    public static String LOG_CHANNEL_ID;
    public static Color EMBED_COLOR;
    public static int PLACE_PROJECT_ID;
    public static int BUTTON_SCORE;
    public static boolean PLACE_VERIFY;
    public static String ENVIRONMENT = "development";

    public static void updateConfig(String key, String value) {
        ConfigDAO configDao = new ConfigDAO();

        try {
            configDao.updateConfig(key, value);
            setClassVariables(key, value);
        } catch (Exception ignored) {
            LOGGER.error("Failed to update config: {{} : {}}", key, value);
            return;
        }

        LOGGER.info("Config updated: {{} : {}}", key, value);
    }

    public static void initConfig() {
        ConfigDAO configDao = new ConfigDAO();
        List<ConfigORM> configEntries = configDao.getConfig();

        for (ConfigORM entry : configEntries) {
            setClassVariables(entry.getKey(), entry.getValue());
        }

        LOGGER.info("Config initialized");
    }

    private static void setClassVariables(String key, String value) {
        switch (key) {
            case "token" -> TOKEN = value;
            case "prefix" -> PREFIX = value;
            case "ownerId" -> OWNER_ID = value;
            case "logChannel" -> LOG_CHANNEL_ID = value;
            case "embedColor" -> EMBED_COLOR = Color.decode(value);
            case "placeProject" -> PLACE_PROJECT_ID = Integer.parseInt(value);
            case "buttonScore" -> BUTTON_SCORE = Integer.parseInt(value);
            case "placeVerify" -> PLACE_VERIFY = Boolean.parseBoolean(value);
        }
    }
}
