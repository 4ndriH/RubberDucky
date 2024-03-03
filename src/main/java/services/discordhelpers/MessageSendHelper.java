package services.discordhelpers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class MessageSendHelper {

    public static void sendMessage(MessageCreateAction mca, int secondsUntilDeletion) {
        mca.queue(msg -> MessageDeleteHelper.deleteMessage(msg, secondsUntilDeletion));
    }

    public static void sendMessageComplete(MessageCreateAction mca, int secondsUntilDeletion) {
        Message msg = mca.complete();
        MessageDeleteHelper.deleteMessage(msg, secondsUntilDeletion);
    }
}
