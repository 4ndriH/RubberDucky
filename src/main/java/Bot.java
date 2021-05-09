import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import resources.CONFIG;
import services.Listener;

import javax.security.auth.login.LoginException;

public class Bot {
    public static void main(String[] args) throws LoginException {
        new Bot();
    }

    private Bot() throws LoginException {
        JDABuilder.createDefault(CONFIG.Token.get(),
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES
        )
                .disableCache(CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOTE
                )
                .addEventListeners(new Listener())
//                .addEventListeners(new FerrisListener())
                .setActivity(Activity.playing("With Duckies"))
                .build();
    }
}
