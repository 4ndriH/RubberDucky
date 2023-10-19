package services.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.SessionRecreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountThreadListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BGListener.class);
    private static boolean spamPingProtection = false;
    private static int lastSent, interruptCount = 60;
    private static ThreadChannel thread;
    public static String listenTo = "742380498986205234";

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().getId().equals("817846061347242026")) {
            return;
        }

        thread = event.getJDA().getGuildById("747752542741725244").getThreadChannelById("996746797236105236");
        checkRecentMessages();
    }

    @Override
    public void onSessionRecreate(@NotNull SessionRecreateEvent event) {
        checkRecentMessages();
        event.getJDA().getGuildById("747752542741725244").getTextChannelById("768600365602963496").sendMessage("<@155419933998579713> did I resume?").queue();
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
                spamPingProtection = false;
            }
        } else if (event.getChannel().getId().equals("819966095070330950")) {
            if (!spamPingProtection && --interruptCount <= 0) {
                checkRecentMessages();

                event.getGuild().getTextChannelById("768600365602963496").sendMessage("<@155419933998579713> RubberDucky detected something weird in https://discord.com/channels/747752542741725244/996746797236105236/" + event.getMessage().getId() + " <a:dinkdonk:1006477116835110942>").queue();
                thread.sendMessage("" + lastSent).queue();
                spamPingProtection = true;
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
