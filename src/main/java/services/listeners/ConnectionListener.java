package services.listeners;

import assets.CONFIG;
import assets.Objects.DeletableMessage;
import commandHandling.commands.place.PlaceDraw;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import services.database.DBHandlerConfig;
import services.database.DBHandlerMessageDeleteTracker;
import services.database.DBHandlerPlace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class ConnectionListener extends ListenerAdapter {
    private static boolean onStartupTasks = true;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);

        if (onStartupTasks) {
            int placeID = Integer.parseInt(DBHandlerConfig.getConfig().get("placeProject"));
            if (placeID != -1) {
                if (DBHandlerPlace.getPlaceProjectIDs().contains(placeID)) {
                    (new Thread(() -> {
                        PlaceDraw.draw(event.getJDA(), placeID);
                    })).start();
                } else {
                    DBHandlerConfig.updateConfig("placeProject", "-1");
                }
            }

            String githubSHA = DBHandlerConfig.getConfig().get("GitHubSHA");
            if (githubSHA != null) {
                event.getJDA().getGuildById(817850050013036605L).getTextChannelById(CONFIG.logChannelID).sendMessage("Restarted with commit " + githubSHA).queue();
                DBHandlerConfig.updateConfig("GitHubSHA", null);
            }

            (new Thread(() -> {
                ArrayList<DeletableMessage> messages = DBHandlerMessageDeleteTracker.getMessagesToDelete();
                Collections.sort(messages);

                long currentSystemTime = System.currentTimeMillis();
                int deletionDelay = 16;

                for (DeletableMessage dm : messages) {
                    Message msg = dm.getMessage(event.getJDA());

                    if (msg != null) {
                        if (dm.deleteLater(currentSystemTime)) {
                            msg.delete().queueAfter(deletionDelay++, TimeUnit.SECONDS);
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
