package services.listeners;

import assets.Config;
import assets.objects.DeletableMessage;
import commandhandling.commands.place.PlaceDraw;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import services.database.daos.MessageDeleteTrackerDAO;
import services.database.daos.PlaceProjectsDAO;
import services.logging.DiscordAppender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static services.discordhelpers.MessageDeleteHelper.deleteMessage;

public class StartupListener extends ListenerAdapter {
    private static boolean onStartupTasks = true;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (onStartupTasks) {
            // restart place drawing
            int placeID = Config.PLACE_PROJECT_ID;
            if (placeID >= 0 && event.getJDA().getSelfUser().getId().equals("817846061347242026")) {
                PlaceProjectsDAO placeProjectsDAO = new PlaceProjectsDAO();
                if (placeProjectsDAO.getProjectIds().contains(placeID)) {
                    (new Thread(() -> PlaceDraw.draw(event.getJDA(), placeID))).start();
                } else {
                    Config.updateConfig("placeProject", "-1");
                }
            } else {
                Config.updateConfig("placeProject", "-1");
            }

            Objects.requireNonNull(Objects.requireNonNull(event.getJDA().getGuildById(817850050013036605L)).getTextChannelById(Config.LOG_CHANNEL_ID))
                    .sendMessage(event.getJDA().getSelfUser().getAsMention() + " started successfully and is ready to go").queue();

            // add command for active dev badge
            event.getJDA().upsertCommand("channelefficiency", "How are channels performing").addOptions(new OptionData(OptionType.STRING, "channel", "Which channel to show")
                    .addChoice("To Infinity And Beyond", "Count")
                    .addChoice("ETH-Place-Bots", "Place")
                    .setRequired(false)
            ).queue();
            DiscordAppender.setJDA(event.getJDA());

            // delete messages that were scheduled for deletion
            (new Thread(() -> {
                MessageDeleteTrackerDAO messageDeleteTrackerDAO = new MessageDeleteTrackerDAO();
                ArrayList<DeletableMessage> messages = messageDeleteTrackerDAO.getMessagesToDelete();
                messageDeleteTrackerDAO.pruneMessageDeleteTracker();

                Collections.sort(messages);
                int deletionDelay = 16;

                for (DeletableMessage dm : messages) {
                    Message msg = dm.getMessage(event.getJDA());

                    if (msg != null) {
                        if (dm.futureDeletion()) {
                            deleteMessage(msg, dm.deleteTime());
                        } else {
                            deleteMessage(msg, deletionDelay++);
                        }
                    }
                }
            })).start();

            onStartupTasks = false;
        }
    }
}
