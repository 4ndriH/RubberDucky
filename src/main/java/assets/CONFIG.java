package assets;

import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerConfig;

import java.util.HashMap;

public enum CONFIG {
    Token("lorem"),
    Prefix("ipsum"),
    OwnerID("dolor"),
    LogChannel("sit"),
    embedColor("amet");

    private static final Logger LOGGER = LoggerFactory.getLogger("Config");
    private String id;

    public static JDA instance;

    CONFIG(String id) {
        this.id = id;
    }

    public String get() {
        return this.id;
    }

    public static void reload() {
        HashMap<String, String> config = DBHandlerConfig.getConfig();
        Token.id = config.get("token");
        Prefix.id = config.get("prefix");
        OwnerID.id = config.get("ownerId");
        LogChannel.id = config.get("logChannel");
        embedColor.id = config.get("embedColor");

        LOGGER.info("Config loaded");
    }
}
