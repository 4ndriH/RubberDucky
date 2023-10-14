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

import java.util.ArrayList;
import java.util.HashMap;

import static services.database.DBHandlerConfig.getConfig;

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
        //listenTo = getConfig().get("CountThreadListenTo");

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
                String botId = thread.getHistory().retrievePast(1).complete().get(0).getAuthor().getId();
                if (botId.equals(listenTo)) {
                    checkRecentMessages();
                }

                event.getGuild().getTextChannelById("768600365602963496").sendMessage("<@155419933998579713> RubberDucky detected something weird in <#996746797236105236> <a:dinkdonk:1006477116835110942>").queue();
                event.getGuild().getTextChannelById("768600365602963496").sendMessage("https://discord.com/channels/747752542741725244/996746797236105236/" + event.getMessage().getId()).queue();
                thread.sendMessage("" + lastSent).queue();
                spamPingProtection = true;
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
