package services;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import services.database.DBHandlerBlacklistedUsers;
import services.database.DBHandlerSnowflakePermissions;
import services.database.DBHandlerWhitelistedChannels;
import services.database.DBHandlerWhitelistedServers;

import java.util.ArrayList;
import java.util.HashMap;

import static services.discordhelpers.ReactionHelper.addReaction;

public class PermissionManager {
    private static HashMap<String, ArrayList<String>> channels = new HashMap<>();
    private static HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> snowflakes = new HashMap<>();
    private static ArrayList<String> blackList = new ArrayList<>();
    private static ArrayList<String> servers = new ArrayList<>();

    public static boolean permissionCheck(CommandContext ctx, CommandInterface cmd) {
        return authenticateOwner(ctx) || snowflakeCheck(ctx, cmd.getNameLC()) || serverCheck(ctx) &&
                !blackListCheck(ctx) && securityClearanceCheck(ctx, cmd) && channelCheck(ctx, cmd.getNameLC());
    }

    public static boolean authenticateOwner(CommandContext ctx) {
        return ctx.getSecurityClearance() == 0;
    }

    public static boolean snowflakeCheck(CommandContext ctx, String command) {
        String discordUserId = ctx.getAuthor().getId();
        String discordServerId = ctx.getGuild().getId();
        String discordChannelId = ctx.getChannel().getId();
        return snowflakes.containsKey(discordUserId) &&
                snowflakes.get(discordUserId).containsKey(discordServerId) &&
                snowflakes.get(discordUserId).get(discordServerId).containsKey(discordChannelId)  &&
                snowflakes.get(discordUserId).get(discordServerId).get(discordChannelId).contains(command);
    }

    public static boolean securityClearanceCheck(CommandContext ctx, CommandInterface cmd) {
        if (ctx.getSecurityClearance() > cmd.getRestrictionLevel()) {
           addReaction(ctx, 4);
           return false;
        }

        return true;
    }

    public static boolean serverCheck(CommandContext ctx) {
        if (!servers.contains(ctx.getGuild().getId())) {
            addReaction(ctx, 3);
            return false;
        }

        return true;
    }

    public static boolean channelCheck(CommandContext ctx, String command) {
        if (ctx.getSecurityClearance() == 3 && channelWhitelistCheck(ctx.getChannel().getId(), command)) {
            addReaction(ctx, 2);
            return false;
        }

        return true;
    }

    public static boolean blackListCheck(CommandContext ctx) {
        if (blackList.contains(ctx.getAuthor().getId())) {
            addReaction(ctx, 1);
            return true;
        }

        return false;
    }

    public static void reload() {
        blackList = DBHandlerBlacklistedUsers.getBlacklistedUsers();
        servers = DBHandlerWhitelistedServers.getWhitelistedServers();
        channels = DBHandlerWhitelistedChannels.getWhitelistedChannels();
        snowflakes = DBHandlerSnowflakePermissions.getSnowflakePermissions();
    }

    public static void initiateLockdown() {
        channels = new HashMap<>();
        servers = new ArrayList<>();
    }

    public static void endLockdown() {
        servers = DBHandlerWhitelistedServers.getWhitelistedServers();
        channels = DBHandlerWhitelistedChannels.getWhitelistedChannels();
    }

    public static ArrayList<String> getBlacklist() {
        return new ArrayList<>(blackList);
    }

    public static ArrayList<String> getWhitelistedServers() {
        return new ArrayList<>(servers);
    }

    public static HashMap<String, ArrayList<String>> getWhitelistedChannels() {
        return new HashMap<>(channels);
    }

    public static HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> getSnowflakes() {
        return new HashMap<>(snowflakes);
    }

    private static boolean channelWhitelistCheck(String discordChannelId, String command) {
        return channels.get(discordChannelId) == null || !channels.get(discordChannelId).contains(command);
    }
}