package services.listeners;

import assets.Config;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordhelpers.EmbedHelper;

import java.time.LocalDate;
import java.util.Objects;
import java.util.List;

import static services.database.DBHandlerCourseReviewVerify.unverifiedReviewsExist;


public class ButtonGameListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ButtonGameListener.class);
    private static int nextNotification = 0;
    private static int myCurrentScore = Config.BUTTON_SCORE;

    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().getId().equals("817846061347242026")) {
            event.getJDA().removeEventListener(this);
        }

        TextChannel channel = Objects.requireNonNull(event.getJDA().getGuildById("817850050013036605")).getTextChannelById("988081117015973918");

        if (channel == null) {
            LOGGER.error("Could not find the channel for unverified reviews.");
            return;
        }

        for (Message message : channel.getIterableHistory()) {
            List<MessageEmbed> embeds = message.getEmbeds();

            if (embeds.isEmpty()) {
                continue;
            }

            if (Objects.requireNonNull(embeds.getFirst().getTitle()).contains("Unverified")) {
                notificationMessageID = message.getId();
                LOGGER.info("Found the notification message for unverified reviews.\nMessage ID: {}", notificationMessageID);
                break;
            }
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

            // This is temporary
            dailyCheckForUnreviewedReviews(event);
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
                Config.updateConfig("ButtonScore", score);
            }

            if (messageContent.contains("has claimed")) {
                nextNotification = 0;
            }
        }
    }

    private LocalDate lastCheckedDate = null;
    public static String notificationMessageID = null;

    private void dailyCheckForUnreviewedReviews(MessageUpdateEvent event) {
        LocalDate currentDate = LocalDate.now();

        if (lastCheckedDate == null || !lastCheckedDate.equals(currentDate)) {
            lastCheckedDate = currentDate;

            if (unverifiedReviewsExist()) {
                Message message = Objects.requireNonNull(Objects.requireNonNull(event.getJDA().getGuildById("817850050013036605")).getTextChannelById("988081117015973918")).sendMessageEmbeds(
                        EmbedHelper.embedBuilder("Unverified Reviews Exist")
                                .setDescription("Go verify them!")
                                .setFooter("Yes, I do it now. Also dont worry about how I do it. I am a bot. I am smart.")
                                .build()
                ).complete();

                notificationMessageID = message.getId();
                LOGGER.info("Notification message for unverified reviews sent.\nMessage ID: {}", notificationMessageID);
            }
        }
    }
}
