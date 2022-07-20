package services.listener;

import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SubstiifyDownListener extends ListenerAdapter {
    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        if (event.getUser().getId().equals("690548834610315286") && event.getNewOnlineStatus().getKey().equals("offline")) {
            event.getJDA().openPrivateChannelById("276462585690193921").complete().sendMessage("Substiify went offline").complete();
        }
    }
}
