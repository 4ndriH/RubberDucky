package services;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerBlacklistedUsers;
import services.database.DBHandlerWhitelistedChannels;
import services.database.DBHandlerWhitelistedServers;

import java.util.ArrayList;
import java.util.HashMap;

import static services.ReactionHelper.addReaction;

public class PermissionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionManager.class);

    public static HashMap<String, ArrayList<String>> channels = new HashMap<>();
    public static ArrayList<String> blackList = new ArrayList<>();
    public static ArrayList<String> servers = new ArrayList<>();

    public static boolean permissionCheck(CommandContext ctx, CommandInterface cmd) {
        return authenticateOwner(ctx) || serverCheck(ctx) && !blackListCheck(ctx) &&
                securityClearanceCheck(ctx, cmd) && channelCheck(ctx, cmd.getNameLC());
    }

    public static boolean authenticateOwner(CommandContext ctx) {
        return ctx.getSecurityClearance() == 0;
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
        if (ctx.getSecurityClearance() == 3 && channelWhitelistCheck(ctx, command)) {
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

    public static boolean coolDownCheck(CommandContext ctx, String command) {
        return CoolDownManager.coolDownCheck(ctx, command);
    }

    public static void reload() {
        blackList = DBHandlerBlacklistedUsers.getBlacklistedUsers();
        servers = DBHandlerWhitelistedServers.getWhitelistedServers();
        channels = DBHandlerWhitelistedChannels.getWhitelistedChannels();

        LOGGER.info("Permissions loaded");
    }

    public static void initiateLockdown() {
        channels = new HashMap<>();
        servers = new ArrayList<>();
    }

    private static boolean channelWhitelistCheck(CommandContext ctx, String command) {
        return channels.get(command) == null || !channels.get(command).contains(ctx.getChannel().getId());
    }
}