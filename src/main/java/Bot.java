import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import resources.CONFIG;
import services.CatchListener;
import services.Listener;
import services.OnStartUp;

import javax.security.auth.login.LoginException;

public class Bot {
    public static void main(String[] args) throws LoginException {
        new OnStartUp();
        new Bot();
    }

    private Bot() throws LoginException {
        JDABuilder.createDefault(CONFIG.Token.get(),
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MEMBERS
                )
                .disableCache(CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOTE
                )
                .addEventListeners(new Listener())
                .addEventListeners(new CatchListener())
//                .addEventListeners(new FerrisListener())
                .setActivity(Activity.playing("With Duckies"))
                .build();
    }
}
