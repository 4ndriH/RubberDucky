package resources;

import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.DatabaseHandler;

import java.util.ArrayList;
import java.util.HashMap;

public enum CONFIG {
    Token("nosy"),
    Prefix("much"),
    OwnerID("?"),
    LogChannel("");

    private static final Logger LOGGER = LoggerFactory.getLogger("Config");
    private String id;
    private static HashMap<String, ArrayList<String>> channels = new HashMap<>();
    private static ArrayList<String> blackList = new ArrayList<>();
    private static ArrayList<String> servers = new ArrayList<>();
    public static JDA instance;

    CONFIG(String id) {
        this.id = id;
    }

    public String get() {
        return this.id;
    }

    public static void reload() {
        blackList = DatabaseHandler.getBlacklist();
        servers = DatabaseHandler.getServers();
        channels = DatabaseHandler.getChannels();

        HashMap<String, String> config = DatabaseHandler.getConfig();
        Token.id = config.get("token");
        Prefix.id = config.get("prefix");
        OwnerID.id = config.get("ownerid");
        LogChannel.id = config.get("logchannel");
        LOGGER.info("Config reloaded");
    }

    public static boolean channelCheck(String command, String channel) {
        return channels.get(command) != null && channels.get(command).contains(channel);
    }

    public static HashMap<String, ArrayList<String>> getChannels() {
        return new HashMap<>(channels);
    }

    public static ArrayList<String> getBlackList() {
        return new ArrayList<>(blackList);
    }

    public static ArrayList<String> getServers() {
        return new ArrayList<>(servers);
    }

    public static void initiateLockdown() {
        channels = new HashMap<>();
        servers = new ArrayList<>();
    }
}
