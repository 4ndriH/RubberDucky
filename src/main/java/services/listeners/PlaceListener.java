package services.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.place.TimelapseHelper;

public class PlaceListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceListener.class);
    private static final String timelapseMatch = "Saved Chunk_";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
//            if (event.getChannel().getId().equals("819966095070330950")) {
//                if (event.getMessage().getContentRaw().contains("817846061347242026")) {
//                    PlaceData.addPixelRequest(event.getAuthor().getId());
//                }
//            }

            if (event.getAuthor().getId().equals("774276700557148170") && event.getMessage().getContentRaw().startsWith(timelapseMatch)) {
                String nr = event.getMessage().getContentRaw().split("_")[1].split("\\.")[0];
                int chunk;
                try {
                    chunk = Integer.parseInt(nr);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Could not parse chunk number", e);
                    return;
                }

                LOGGER.info("Chunk number " + chunk + " is available", new InterruptedException());
                int finalChunk = chunk;
                (new Thread(() -> TimelapseHelper.generate(finalChunk, event))).start();
            }
        }
    }
}
