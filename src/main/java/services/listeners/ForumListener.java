package services.listeners;

import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForumListener extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(ForumListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getChannelType().isThread()) {
            LOGGER.info("thread channel | "+ event.getChannel().asThreadChannel().getParentChannel().getName());

            if (event.getChannel().asThreadChannel().getParentChannel().getName().equalsIgnoreCase("bots-testing")) {
                LOGGER.info("success");
                if (System.currentTimeMillis() - event.getChannel().getTimeCreated().toInstant().toEpochMilli() < 1000) {
                    event.getMessage().addReaction(Emoji.fromFormatted("bunnyvibes:989952440126296116")).queue();
                    LOGGER.info("bunny vibes added");
                } else {
                    LOGGER.info("older than 1 second");
                }
            }
        }
    }
}
