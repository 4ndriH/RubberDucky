package services.discordhelpers;

import commandhandling.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class MessageSendHelper {

    public static void sendMessage(CommandContext ctx, MessageCreateAction mca, int secondsUntilDeletion) {
        mca.queue(msg -> MessageDeleteHelper.deleteMessagePersistenceCheck(ctx, msg, secondsUntilDeletion));
    }

    public static void sendMessageComplete(MessageCreateAction mca, int secondsUntilDeletion) {
        Message msg = mca.complete();
        MessageDeleteHelper.deleteMessage(msg, secondsUntilDeletion);
    }
}
