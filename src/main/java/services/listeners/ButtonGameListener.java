package services.listeners;

import assets.Config;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ButtonGameListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ButtonGameListener.class);
    private static int nextNotification = 0;
    private static int myCurrentScore = Config.BUTTON_SCORE;

    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().getId().equals("817846061347242026")) {
            event.getJDA().removeEventListener(this);
        }
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        Message message = event.getMessage();

        if (event.getAuthor().getId().equals("778731540359675904") && message.getContentRaw().contains("button")) {
            int buttonScore = Integer.parseInt(message.getActionRows().get(0).getButtons().get(0).getLabel());

            if (buttonScore > myCurrentScore) {
                if (--nextNotification <= 0) {
                    event.getJDA().openPrivateChannelById("155419933998579713").complete().sendMessage(
                            "You can claim the button [" + myCurrentScore + " -> " + buttonScore + "]\n" +
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
                LOGGER.debug("Button Score Updated. New Score: " + score);
                myCurrentScore = Integer.parseInt(score);
                Config.updateConfig("buttonScore", score);
            }

            if (messageContent.contains("has claimed")) {
                //event.getJDA().openPrivateChannelById("155419933998579713").complete().sendMessage(
                //        "The button will be claimable on <t:" + (System.currentTimeMillis() / 1000 + 60L * myCurrentScore) + ":f>"
                //).queue();

                nextNotification = 0;
            }
        }
    }
}
