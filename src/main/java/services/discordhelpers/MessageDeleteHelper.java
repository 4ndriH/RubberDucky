package services.discordhelpers;

import commandhandling.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import services.database.daos.MessageDeleteTrackerDAO;

import java.util.concurrent.TimeUnit;

public class MessageDeleteHelper {
    public static void deleteMessage(Message msg, int delay) {
        try {
            MessageDeleteTrackerDAO messageDeleteTrackerDAO = new MessageDeleteTrackerDAO();
            messageDeleteTrackerDAO.addMessageToTracker(msg.getGuild().getId(), msg.getChannel().getId(), msg.getId(), System.currentTimeMillis() + delay * 1000L);
            msg.delete().queueAfter(delay, TimeUnit.SECONDS, null, failure -> {});
        } catch (Exception ignored) {}
    }

    public static void deleteMessagePersistenceCheck(CommandContext ctx, Message msg, int delay) {
        if (!ctx.isPersistent()) {
            deleteMessage(msg, delay);
        }
    }
}
