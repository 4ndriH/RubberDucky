package services.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CountThreadListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountThreadListener.class);
    private static ScheduledExecutorService countThreadExecutor;
    private static long EXPONENTIAL_BACKOFF = 60_000;
    public static int lastSent;
    private static ThreadChannel thread;
    public static String listenTo = "742380498986205234";
    private static long lastMessageTime = 0;
    private static int restartAttempts = 0;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().getId().equals("817846061347242026")) {
            event.getJDA().removeEventListener(this);
            return;
        }

        thread = Objects.requireNonNull(event.getJDA().getGuildById("747752542741725244")).getThreadChannelById("996746797236105236");

        countThreadExecutor = Executors.newSingleThreadScheduledExecutor();
        countThreadExecutor.schedule(createRunnable(), EXPONENTIAL_BACKOFF, TimeUnit.MILLISECONDS);

        checkRecentMessages();
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        countThreadExecutor.shutdownNow();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("996746797236105236")) {
            if (event.getAuthor().getId().equals(listenTo)) {
                try {
                    int nextNumber = Integer.parseInt(event.getMessage().getContentRaw()) + 1;

                    if (nextNumber > lastSent) {
                        event.getChannel().sendMessage("" + nextNumber).queue();
                        lastMessageTime = System.currentTimeMillis();
                        lastSent = nextNumber;

                        if (EXPONENTIAL_BACKOFF > 60_000) {
                            if (EXPONENTIAL_BACKOFF > 240_000) {
                                LOGGER.info("Count Thread has been restarted");
                            }

                            EXPONENTIAL_BACKOFF = 60_000;
                            restartAttempts = 0;

                            countThreadExecutor.shutdownNow();
                            countThreadExecutor = Executors.newSingleThreadScheduledExecutor();

                            countThreadExecutor.schedule(createRunnable(), EXPONENTIAL_BACKOFF, TimeUnit.MILLISECONDS);
                        }
                    }
                } catch (Exception ignored) {}
            }

            if (!event.getAuthor().isBot()) {
                event.getMessage().delete().queue();
            }
        }
    }

    public static void checkRecentMessages() {
        for (Message message : thread.getHistory().retrievePast(5).complete()) {
            try {
                String authorId = message.getAuthor().getId();
                if (authorId.equals(listenTo)) {
                    lastSent = Integer.parseInt(message.getContentRaw()) + 1;
                } else if (authorId.equals("817846061347242026")) { // self
                    lastSent = Integer.parseInt(message.getContentRaw());
                }

                if (lastSent != 0) {
                    thread.sendMessage("" + lastSent).queue();
                    return;
                }
            } catch (Exception ignored) {}
        }
    }

    private static Runnable createRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    if (System.currentTimeMillis() - lastMessageTime > EXPONENTIAL_BACKOFF) {
                        if (EXPONENTIAL_BACKOFF == 480_000) {
                            LOGGER.warn("Count thread has been interrupted. Attempting restart... \n[failed attempts: " + restartAttempts + "]");
                        }

                        thread.sendMessage("" + lastSent).queue();
                        restartAttempts++;

                        if (EXPONENTIAL_BACKOFF < 3_840_000) {
                            EXPONENTIAL_BACKOFF *= 2;
                        }
                    }

                    countThreadExecutor.schedule(this, EXPONENTIAL_BACKOFF, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    LOGGER.error("Count thread executor crashed", e);
                }
            }
        };
    }
}
