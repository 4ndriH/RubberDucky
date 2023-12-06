package services.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountThreadListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BGListener.class);
    private static int EXPONENTIAL_BACKOFF = 60;
    public static int lastSent, interruptCount;
    private static ThreadChannel thread;
    public static String listenTo = "742380498986205234";

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().getId().equals("817846061347242026")) {
            return;
        }

        interruptCount = EXPONENTIAL_BACKOFF;

        thread = event.getJDA().getGuildById("747752542741725244").getThreadChannelById("996746797236105236");
        checkRecentMessages();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("996746797236105236")) {
            if (event.getAuthor().getId().equals(listenTo)) {
                try {
                    int nextNumber = (Integer.parseInt(event.getMessage().getContentRaw()) + 1);

                    if (nextNumber > lastSent) {
                        event.getChannel().sendMessage("" + nextNumber).queue();
                        lastSent = nextNumber;
                    }
                } catch (Exception ignored) {}
            }

            if (!event.getAuthor().isBot()) {
                event.getMessage().delete().queue();
            }

            if (interruptCount < 60) {
                interruptCount++;
            } else {
                if (interruptCount < 2 * EXPONENTIAL_BACKOFF) {
                    interruptCount++;
                } else {
                    interruptCount = 60;
                    EXPONENTIAL_BACKOFF = 60;
                }
            }
        } else if (event.getChannel().getId().equals("819966095070330950")) {
            if (--interruptCount <= 0) {
                LOGGER.warn("Count thread interrupted, resuming...", new InterruptedException());

                checkRecentMessages();

                if (EXPONENTIAL_BACKOFF < 960) {
                    EXPONENTIAL_BACKOFF *= 2;
                }

                interruptCount = EXPONENTIAL_BACKOFF;
            }
        }
    }

    public static void checkRecentMessages() {
        for (Message message : thread.getHistory().retrievePast(5).complete()) {
            try {
                String authorId = message.getAuthor().getId();
                if (authorId.equals(listenTo)) {
                    lastSent = Integer.parseInt(message.getContentRaw()) + 1;
                } else if (authorId.equals("817846061347242026")) { // self
                    lastSent = Integer.parseInt(message.getContentRaw());
                }

                if (lastSent != 0) {
                    thread.sendMessage("" + lastSent).queue();
                    return;
                }
            } catch (Exception ignored) {}
        }
    }
}
