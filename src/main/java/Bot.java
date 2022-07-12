import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import assets.CONFIG;
import services.database.ConnectionPool;
import services.database.DBHandlerConfig;
import services.listener.*;
import services.onStartup.StartUp;

import javax.security.auth.login.LoginException;

public class Bot {
    public static void main(String[] args) throws LoginException {
        StartUp.checks();
        new ConnectionPool();
        StartUp.loadEssentials();
        StartUp.updateToken();
        DBHandlerConfig.incrementUptimeCounter();
        CONFIG.instance = connectToDiscord();
    }

    private static JDA connectToDiscord() throws LoginException {
        return JDABuilder.createDefault(CONFIG.Token.get(),
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MEMBERS
                )
                .disableCache(CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOJI,
                        CacheFlag.STICKER
                )
                .addEventListeners(new Listener())
                .addEventListeners(new CatchListener())
                .addEventListeners(new ButtonListener())
                .addEventListeners(new ConnectionListener())
                .addEventListeners(new BGListener())
//                .addEventListeners(new CountThreadListener())
                .setActivity(Activity.playing("With Duckies"))
                .build();
    }
}
