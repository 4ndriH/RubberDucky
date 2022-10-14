package services.listeners;

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
//        if (!event.getAuthor().isBot()) {
//            LOGGER.info("forum post?\nchannel name: " + event.getChannel().getName() + "\nchannel type" + event.getChannelType());
//            LOGGER.info("" + event.getChannelType());
//        }


    }

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        LOGGER.info("name: " + event.getChannel().getName() + "\nid: " + event.getChannel().getId() + "\ntype: " + event.getChannelType() + "\nparent channel" + event.getChannel().asThreadChannel().getParentChannel());
//        if (event.getChannel().getName().equals("bunnyvibe")) {
//
//        }
    }
}
