package services;

import net.dv8tion.jda.api.entities.Message;
import services.database.DBHandlerMessageDeleteTracker;

import java.util.concurrent.TimeUnit;

public class MessageDeleteHelper {
    public static void deleteMsg(Message msg, int seconds) {
        try {
            if (seconds >= 0) {
                msg.delete().queueAfter(seconds, TimeUnit.SECONDS, null, failure -> {});
            }
        } catch (Exception ignored) {}

        if (seconds > 0) {
            DBHandlerMessageDeleteTracker.insertDeleteMessage(msg.getGuild().getId(), msg.getChannel().getId(), msg.getId(),
                    System.currentTimeMillis() + seconds * 1000L);
        }
    }
}
