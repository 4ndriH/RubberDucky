package services.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static services.database.DBHandlerConfig.getConfig;
import static services.database.DBHandlerConfig.updateConfig;

public class CountThreadListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BGListener.class);
    private static String listenTo;
    private static int lastSent;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().getId().equals("817846061347242026")) {
            return;
        }

        ThreadChannel thread = event.getJDA().getGuildById("747752542741725244").getThreadChannelById("996746797236105236");
        listenTo = getConfig().get("CountThreadListenTo");

        for (Message message : thread.getHistory().retrievePast(1).complete()) {
            try {
                if (message.getAuthor().getId().equals(listenTo)) {
                    lastSent = Integer.parseInt(message.getContentRaw()) + 1;
                    thread.sendMessage("" + lastSent).queue();
                }
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("996746797236105236")) {
            if (event.getAuthor().getId().equals(listenTo)) {
                try {
                    int nextNumber = (Integer.parseInt(event.getMessage().getContentRaw()) + 1);

                    if (nextNumber > lastSent) {
                        event.getThreadChannel().sendMessage("" + nextNumber).queue();
                        lastSent = nextNumber;
                    }
                } catch (Exception ignored) {}
            }

            if (!event.getAuthor().isBot()) {
                event.getMessage().delete().queue();
            }
        } else if (event.getAuthor().getId().equals("155419933998579713") && event.getMessage().getContentRaw().contains("rdwatch")) {
            updateConfig("CountThreadListenTo", event.getMessage().getContentRaw().replace("rdwatch ", ""));

            event.getChannel().sendMessage("I am watching you <@" + (listenTo = getConfig().get("CountThreadListenTo")) + "> <:bustinGood:747783377171644417>").queue(
                    (msg) -> msg.delete().queueAfter(60, TimeUnit.SECONDS)
            );
        }

    }
}
