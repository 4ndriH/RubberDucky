package services;

import services.database.dbHandlerPermissions;
import commandHandling.CommandContext;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.*;

public class PermissionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionManager.class);

    public static boolean permissionCheck(CommandContext ctx, String invoke) {
        return authOwner(ctx) || serverCheck(ctx) && !botCheck(ctx)
                && roleCheck(ctx, invoke) && channelCheck(ctx, invoke) && !blackListCheck(ctx);
    }

    public static boolean authOwner (CommandContext ctx) {
        return ctx.getAuthor().getId().equals(config.get("OWNER_ID"));
    }

    public static boolean botCheck (CommandContext ctx) {
        return ctx.getMessage().getAuthor().isBot();
    }

    public static boolean blackListCheck (CommandContext ctx) {
        return dbHandlerPermissions.blackList(ctx.getAuthor().getId());
    }

    public static boolean serverCheck (CommandContext ctx) {
        return dbHandlerPermissions.server(ctx.getGuild().getId());
    }

    public static boolean roleCheck (CommandContext ctx, String invoke) {
        ArrayList<String> permittedRoles = dbHandlerPermissions.roles(invoke);

        for (String rl : permittedRoles) {
            if (ctx.getMember().getRoles().contains(ctx.getGuild().getRoleById(rl))) {
                return true;
            }
        }

        return permittedRoles.size() == 0;
    }

    public static boolean channelCheck (CommandContext ctx, String invoke) {
        ArrayList<String> permittedChannels = dbHandlerPermissions.channels(invoke);

        return permittedChannels.size() == 0 || permittedChannels.contains(ctx.getChannel().getId());
    }
}
