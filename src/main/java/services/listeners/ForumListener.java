package services.listeners;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForumListener extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(ForumListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            LOGGER.info("forum post?\nchannel name: " + event.getChannel().getName() + "\nchannel type" + event.getChannelType());
        }

        if (event.getChannel().getName().equals("bunnyvibe")) {
            event.getMessage().addReaction(Emoji.fromFormatted("a:bunnyvibes:989952440126296116")).queue();
            event.getMessage().addReaction(Emoji.fromFormatted("bunnyvibes:989952440126296116")).queue();
        }
    }
}
