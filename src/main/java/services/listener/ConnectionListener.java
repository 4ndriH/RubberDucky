package services.listener;

import commandHandling.commands.publicCommands.place.PlaceDraw;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import services.database.DatabaseHandler;

public class ConnectionListener extends ListenerAdapter {
    private static boolean onStartupTasks = true;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);

        if (onStartupTasks) {
            if (!DatabaseHandler.getConfig().get("placeProject").equals("-1")) {
                (new Thread(() -> {
                    PlaceDraw.draw(event.getJDA(), Integer.parseInt(DatabaseHandler.getConfig().get("placeProject")));
                })).start();
            }

            onStartupTasks = false;
        }
    }
}
