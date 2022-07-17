package services.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import services.place.PlaceData;

public class PlaceListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromThread() && event.getAuthor().getId().equals("155419933998579713") && event.getMessage().getContentRaw().equals("<@817846061347242026>")) {
            PlaceData.threads.add(event.getThreadChannel());
        }
    }
}
