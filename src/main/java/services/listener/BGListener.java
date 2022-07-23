package services.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static services.database.DBHandlerConfig.getConfig;
import static services.database.DBHandlerConfig.updateConfig;

public class BGListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BGListener.class);
    private static int nextNotification = 0;

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        Message message = event.getMessage();

        if (event.getAuthor().getId().equals("778731540359675904") && message.getContentRaw().contains("button")) {
            int buttonScore = Integer.parseInt(message.getActionRows().get(0).getButtons().get(0).getLabel());
            int myCurrentScore = Integer.parseInt(getConfig().get("ButtonScore"));

            if (buttonScore > myCurrentScore) {
                if (--nextNotification <= 0) {
                    event.getJDA().openPrivateChannelById("155419933998579713").complete().sendMessage(
                            "Button is ready [" + myCurrentScore + " -> " + buttonScore + "]\n" +
                                "https://discord.com/channels/747752542741725244/" + event.getChannel().getId() +
                                "/" + event.getMessage().getId()
                    ).queue();
                    nextNotification = 10;
                }
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().getId().equals("778731540359675904") && event.getMessage().getContentRaw().contains("claimed")) {
            String messageContent = event.getMessage().getContentRaw();

            if (messageContent.contains("155419933998579713")) {
                String score = messageContent.replace("155419933998579713", "").replaceAll("\\D", "");
                LOGGER.info("Button Score Updated. New Score: " + score);
                updateConfig("ButtonScore", score);
            }

            if (messageContent.contains("has claimed")) {
                nextNotification = 0;
            }
        }
    }
}
