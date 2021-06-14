package services;

import commandHandling.CommandContext;
import resources.CONFIG;

public class PermissionManager {
    public static boolean permissionCheck(CommandContext ctx, String invoke, CommandManager cm) {
        return authenticateOwner(ctx) || serverCheck(ctx) && !blackListCheck(ctx) && channelCheck(ctx, invoke)
                && !cm.getCommand(invoke).isOwnerOnly();

    }

    public static boolean authenticateOwner(CommandContext ctx) {
        return ctx.getAuthor().getId().equals(CONFIG.OwnerID.get());
    }

    public static boolean blackListCheck (CommandContext ctx) {
        return CONFIG.getBlackList().contains(ctx.getAuthor().getId());
    }

    public static boolean channelCheck (CommandContext ctx, String invoke) {
        return CONFIG.commandChannelCheck(invoke, ctx.getChannel().getId());
    }

    public static boolean serverCheck (CommandContext ctx) {
        return CONFIG.getServers().contains(ctx.getGuild().getId());
    }
}