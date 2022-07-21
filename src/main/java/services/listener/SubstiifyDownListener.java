package services.listener;

import com.iwebpp.crypto.TweetNaclFast;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SubstiifyDownListener extends ListenerAdapter {
    private static final HashMap<String, ScheduledFuture<?>> notificationGracePeriod = new HashMap<>();
    private static final ArrayList<String> trackedAccounts = new ArrayList<>(){{
        add("690548834610315286"); // Substiify
    }};
    private static final HashMap<String, String> sendTo = new HashMap<>(){{
       put("690548834610315286", "276462585690193921"); // Substiify -> Jackra1n
    }};

    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        String discordUserId = event.getUser().getId();

        if (trackedAccounts.contains(discordUserId)) {
            if (event.getNewOnlineStatus().getKey().equals("offline")) {
                if (!notificationGracePeriod.containsKey(discordUserId) || notificationGracePeriod.get(discordUserId).isDone()) {
                    ScheduledFuture<?> test = event.getJDA().openPrivateChannelById(sendTo.get(discordUserId)).complete().sendMessage(event.getUser().getAsTag() + " went offline").queueAfter(60, TimeUnit.SECONDS);
                    notificationGracePeriod.put(event.getUser().getId(), test);
                }
            } else if (event.getNewOnlineStatus().getKey().equals("online")) {
                if (notificationGracePeriod.containsKey(discordUserId)) {
                    notificationGracePeriod.get(discordUserId).cancel(true);
                    notificationGracePeriod.remove(discordUserId);
                }
            }
        }
    }
}
