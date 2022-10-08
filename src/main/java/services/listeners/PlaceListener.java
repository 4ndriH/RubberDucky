package services.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import services.place.PlaceData;

public class PlaceListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() && event.getChannel().getId().matches("819966095070330950|955751651942211604")) {
            if (event.getMessage().getContentRaw().contains("817846061347242026")) {
                PlaceData.addPixelRequest(event.getAuthor().getId());
            }
        }
    }
}
