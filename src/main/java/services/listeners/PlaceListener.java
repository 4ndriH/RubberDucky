package services.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.place.TimelapseHelper;

import java.util.Queue;

public class PlaceListener extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceListener.class);
    private static final String timelapseMatch = "Saved Chunk_";
    private static Queue<String> logBuffer = new CircularFifoQueue<>(8);

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
                int chunk = 0;
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

            if (event.getAuthor().getId().equals("735170037282898061") && event.getMessage().getContentRaw().startsWith(".place setpixel 418 718")) {
                LOGGER.info("rafeal restarted from the top");

                StringBuilder sb = new StringBuilder();
                for (String s : logBuffer) {
                    sb.append(s).append("\n");
                }
                event.getJDA().getGuildById("817850050013036605").getTextChannelById("841393155478650920").sendMessage(sb).queue();
            }
        } else if (event.getAuthor().getId().equals("378255099748679681")) {
            logBuffer.add(event.getMessage().getContentRaw());
        }
    }
}
