package services.discordhelpers;

import commandhandling.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import services.database.DBHandlerMessageDeleteTracker;

import java.util.concurrent.TimeUnit;

public class MessageDeleteHelper {
    public static void deleteMsg(Message msg, int delay) {
        try {
            DBHandlerMessageDeleteTracker.insertDeleteMessage(msg.getGuild().getId(), msg.getChannel().getId(), msg.getId(), System.currentTimeMillis() + delay * 1000L);
            msg.delete().queueAfter(delay, TimeUnit.SECONDS, null, failure -> {});
        } catch (Exception ignored) {}
    }

    public static void deleteMsg(CommandContext ctx, int delay) {
        if (ctx.checkPersistence()) {
            deleteMsg(ctx.getMessage(), delay);
        }
    }

    public static void deleteMsg(CommandContext ctx, Message msg, int delay) {
        if (ctx.checkPersistence()) {
            deleteMsg(msg, delay);
        }
    }
}
