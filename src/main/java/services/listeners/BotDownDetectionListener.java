package services.listeners;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BotDownDetectionListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotDownDetectionListener.class);
    private static final HashMap<String, ScheduledFuture<?>> notificationGracePeriod = new HashMap<>();
    private static final ArrayList<String> trackedAccounts = new ArrayList<>(){{
        add("690548834610315286"); // Substiify
    }};
    private static final HashMap<String, String> sendTo = new HashMap<>(){{
       put("690548834610315286", "276462585690193921"); // Substiify -> Jackra1n
    }};

    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().getId().equals("817846061347242026")) {
            event.getJDA().removeEventListener(this);
        }
    }

    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        String discordUserId = event.getUser().getId();

        if (trackedAccounts.contains(discordUserId)) {
            if (event.getNewOnlineStatus().getKey().equals("offline")) {
                if (!notificationGracePeriod.containsKey(discordUserId) || notificationGracePeriod.get(discordUserId).isDone()) {
                    //LOGGER.info(event.getUser().getAsTag() + " went offline, triggered 60s notification countdown");
                    ScheduledFuture<?> test = event.getJDA().openPrivateChannelById(sendTo.get(discordUserId)).complete().sendMessage(event.getUser().getAsTag() + " went offline").queueAfter(60, TimeUnit.SECONDS);
                    notificationGracePeriod.put(event.getUser().getId(), test);
                }
            } else if (event.getNewOnlineStatus().getKey().equals("online")) {
                if (notificationGracePeriod.containsKey(discordUserId) && !notificationGracePeriod.get(discordUserId).isDone()) {
                    //LOGGER.info(event.getUser().getAsTag() + " came back online within 60s, canceled notification");
                    notificationGracePeriod.get(discordUserId).cancel(true);
                    notificationGracePeriod.remove(discordUserId);
                }
            }
        }
    }
}
