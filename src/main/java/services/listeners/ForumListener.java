package services.listeners;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ForumListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getChannelType().isThread()) {
            if (event.getChannel().asThreadChannel().getParentChannel().getId().equals("1030381069553385533")) {
                if (System.currentTimeMillis() - event.getChannel().getTimeCreated().toInstant().toEpochMilli() < 1000) {
                    event.getMessage().addReaction(Emoji.fromFormatted("bunnyvibes:989952440126296116")).queue();
                }
            }
        }
    }
}
