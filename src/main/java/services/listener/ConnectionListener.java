package services.listener;

import commandHandling.commands.publicCommands.place.PlaceDraw;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import resources.CONFIG;
import services.database.DatabaseHandler;

public class ConnectionListener extends ListenerAdapter {
    private static boolean onStartupTasks = true;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);

        //maybe change this to use the GitHubSHA instead of a boolean
        if (onStartupTasks) {
            int placeID = Integer.parseInt(DatabaseHandler.getConfig().get("placeProject"));
            if (placeID != -1) {
                if (DatabaseHandler.getPlaceProjectIDs().contains(placeID)) {
                    (new Thread(() -> {
                        PlaceDraw.draw(event.getJDA(), placeID);
                    })).start();
                } else {
                    DatabaseHandler.updateConfig("placeProject", "-1");
                }
            }

            String githubSHA = DatabaseHandler.getConfig().get("GitHubSHA");
            if (githubSHA.length() > 0) {
                event.getJDA().getGuildById(817850050013036605L).getTextChannelById(CONFIG.LogChannel.get())
                        .sendMessage("Restarted with commit " + githubSHA).queue();
                DatabaseHandler.updateConfig("GitHubSHA", "");
            }

            onStartupTasks = false;
        }
    }
}
