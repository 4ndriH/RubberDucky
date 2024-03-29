package services.listeners;

import assets.Config;
import assets.objects.DeletableMessage;
import commandhandling.commands.place.PlaceDraw;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import services.database.DBHandlerConfig;
import services.database.DBHandlerMessageDeleteTracker;
import services.database.DBHandlerPlace;
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
            int placeID = Integer.parseInt(DBHandlerConfig.getConfig().get("placeProject"));
            if (placeID != -1) {
                if (DBHandlerPlace.getPlaceProjectIDs().contains(placeID)) {
                    (new Thread(() -> PlaceDraw.draw(event.getJDA(), placeID))).start();
                } else {
                    DBHandlerConfig.updateConfig("placeProject", "-1");
                }
            }

            Objects.requireNonNull(Objects.requireNonNull(event.getJDA().getGuildById(817850050013036605L)).getTextChannelById(Config.logChannelID))
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
                ArrayList<DeletableMessage> messages = DBHandlerMessageDeleteTracker.getMessagesToDelete();
                Collections.sort(messages);

                long currentSystemTime = System.currentTimeMillis();
                int deletionDelay = 16;

                for (DeletableMessage dm : messages) {
                    Message msg = dm.getMessage(event.getJDA());

                    if (msg != null) {
                        if (dm.deleteLater(currentSystemTime)) {
                            deleteMessage(msg, deletionDelay++);
                        } else {
                            msg.delete().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
                        }
                    }
                }
            })).start();

            onStartupTasks = false;
        }
    }
}
