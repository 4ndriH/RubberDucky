package services.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerEfficiencyLog;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EfficiencyTrackerListener extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(EfficiencyTrackerListener.class);
    private ScheduledExecutorService sqlExecutor;
    private int ethPlaceBots;
    private int countThread;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        sqlExecutor = Executors.newSingleThreadScheduledExecutor();

        Runnable r = () -> {
            try {
                DBHandlerEfficiencyLog.addDataPoint(ethPlaceBots, countThread);
                ethPlaceBots = countThread = 0;
            } catch (Exception e) {
                LOGGER.error("SQL Executor experienced an issue", e);
            }
        };

        try {
            sqlExecutor.scheduleAtFixedRate (r , 0L , 1L , TimeUnit.MINUTES);
        } catch (Exception e) {
            LOGGER.info("error executor");
        }
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        sqlExecutor.shutdown();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String threadId = event.getChannel().getId();

        if (threadId.equals("819966095070330950")) {
            ethPlaceBots++;
        } else if (threadId.equals("996746797236105236")) {
            countThread++;
        }
    }
}
