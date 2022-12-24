package services.listeners;

import assets.CONFIG;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ReactionListener extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(ListenerAdapter.class);

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getUser().getId().equals(CONFIG.ownerID) && event.getReaction().getEmoji().getFormatted().equals("<:DuckyShut:1056189438142717952>")) {
            Member member = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getMember();
            member.timeoutFor(60L, TimeUnit.MINUTES).complete();
            LOGGER.info("Timed out " + member.getEffectiveName() + " for 60 minutes");
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (event.getUser().getId().equals(CONFIG.ownerID) && event.getReaction().getEmoji().getFormatted().equals("<:DuckyShut:1056189438142717952>")) {
            Member member = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getMember();
            member.timeoutFor(1L, TimeUnit.SECONDS).complete();
            LOGGER.info("Removed Time out from " + member.getEffectiveName());
        }
    }
}
