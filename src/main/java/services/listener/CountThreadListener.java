package services.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static services.database.DBHandlerConfig.getConfig;

public class CountThreadListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BGListener.class);
    private static ThreadChannel thread;
    public static String listenTo;
    private static int lastSent;
    private static int interruptCount = 60;
    private static boolean spamPingProtection = false;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().getId().equals("817846061347242026")) {
            return;
        }

        thread = event.getJDA().getGuildById("747752542741725244").getThreadChannelById("996746797236105236");
        listenTo = getConfig().get("CountThreadListenTo");

        checkRecentMessages();
    }

    @Override
    public void onReconnected(@NotNull ReconnectedEvent event) {
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

            if (interruptCount < 60)  {
                interruptCount++;
            }
        } else if (event.getChannel().getId().equals("819966095070330950")) {
            if (!spamPingProtection && --interruptCount <= 0) {
                event.getGuild().getTextChannelById("768600365602963496").sendMessage("<#996746797236105236> stopped <@155419933998579713>").queue();
                spamPingProtection = true;
                interruptCount = 60;
            }
        }
    }

    public static void checkRecentMessages() {
        for (Message message : thread.getHistory().retrievePast(1).complete()) {
            try {
                if (message.getAuthor().getId().equals(listenTo)) {
                    lastSent = Integer.parseInt(message.getContentRaw()) + 1;
                    thread.sendMessage("" + lastSent).queue();
                }
            } catch (Exception ignored) {}
        }
    }
}
