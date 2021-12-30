package resources;

import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DatabaseHandler;

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
        HashMap<String, String> config = DatabaseHandler.getConfig();
        Token.id = config.get("token");
        Prefix.id = config.get("prefix");
        OwnerID.id = config.get("ownerid");
        LogChannel.id = config.get("logchannel");
        embedColor.id = config.get("embedColor");

        LOGGER.info("Config loaded");
    }
}
