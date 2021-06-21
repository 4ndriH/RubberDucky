package resources;

import java.util.ArrayList;
import java.util.HashMap;

public enum CONFIG {
    Token("nosy"),
    Prefix("much"),
    OwnerID("?");

    private String id;
    private static HashMap<String, ArrayList<String>> channels = new HashMap<>();
    private static ArrayList<String> blackList = new ArrayList<>();
    private static ArrayList<String> servers = new ArrayList<>();

    CONFIG(String id) {
        this.id = id;
    }

    public String get() {
        return this.id;
    }

    public static void reload() {
        blackList = services.database.dbHandlerPermissions.getBlacklist();
        servers = services.database.dbHandlerPermissions.getServers();
        channels = services.database.dbHandlerPermissions.getChannels();

        HashMap<String, String> config = services.database.dbHandlerConfig.getConfig();
        Token.id = config.get("token");
        Prefix.id = config.get("prefix");
        OwnerID.id = config.get("ownerid");
    }

    public static HashMap<String, ArrayList<String>> getChannels() {
        return new HashMap<>(channels);
    }

    public static boolean commandChannelCheck(String command, String channel) {
        return channels.get(command) != null && channels.get(command).contains(channel);
    }

    public static ArrayList<String> getBlackList() {
        return new ArrayList<>(blackList);
    }

    public static ArrayList<String> getServers() {
        return new ArrayList<>(servers);
    }

    public static void initiateLockdown() {
        channels = new HashMap<>();
    }
}
