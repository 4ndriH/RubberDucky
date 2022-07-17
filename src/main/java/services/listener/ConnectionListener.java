package services.listener;

import assets.CONFIG;
import commandHandling.commands.place.PlaceDraw;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import services.database.DBHandlerConfig;
import services.database.DBHandlerMessageDeleteTracker;
import services.database.DBHandlerPlace;

import java.util.ArrayList;
import java.util.HashMap;

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
                event.getJDA().getGuildById(817850050013036605L).getTextChannelById(CONFIG.LogChannel.get())
                        .sendMessage("Restarted with commit " + githubSHA).queue();
                DBHandlerConfig.updateConfig("GitHubSHA", null);
            }

            (new Thread(() -> {
                HashMap<String, HashMap<String, ArrayList<String>>> messages = DBHandlerMessageDeleteTracker.getMessagesToDelete();

                for (String server : messages.keySet()) {
                    for (String channel : messages.get(server).keySet()) {
                        System.out.println(server + " " + channel + " - " + messages.get(server).get(channel));
                        event.getJDA().getGuildById(server).getTextChannelById(channel)
                                .purgeMessagesById(messages.get(server).get(channel));
                    }
                }
            })).start();
            onStartupTasks = false;
        }
    }
}
