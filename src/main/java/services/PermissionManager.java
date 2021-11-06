package services;

import commandHandling.CommandContext;
import resources.CONFIG;

public class PermissionManager {
    public static boolean permissionCheck(CommandContext ctx, String invoke, CommandManager cm) {
        return authenticateOwner(ctx) || !cm.getCommand(invoke).isOwnerOnly() && serverCheck(ctx)
                && !blackListCheck(ctx) && channelCheck(ctx, invoke) && !coolDownCheck(ctx, invoke);
    }

    public static boolean authenticateOwner(CommandContext ctx) {
        return ctx.getAuthor().getId().equals(CONFIG.OwnerID.get());
    }

    public static boolean blackListCheck(CommandContext ctx) {
        return CONFIG.blackList.contains(ctx.getAuthor().getId());
    }

    public static boolean channelCheck(CommandContext ctx, String invoke) {
        return CONFIG.channels.get(invoke) != null && CONFIG.channels.get(invoke).contains(ctx.getChannel().getId());
    }

    public static boolean serverCheck(CommandContext ctx) {
        return CONFIG.servers.contains(ctx.getGuild().getId());
    }

    public static boolean coolDownCheck(CommandContext ctx, String command) {
        return CoolDownManager.coolDownCheck(ctx, command);
    }
}