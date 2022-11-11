import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import assets.CONFIG;
import services.database.ConnectionPool;
import services.database.DBHandlerConfig;
import services.listeners.*;
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
        CONFIG.instance.upsertCommand("ping", "Pong!").queue();
    }

    private static JDA connectToDiscord() throws LoginException {
        return JDABuilder.createDefault(CONFIG.Token.get()
                ).enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.GUILD_PRESENCES
                ).enableCache(
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ONLINE_STATUS
                ).setMemberCachePolicy(
                        MemberCachePolicy.ONLINE
                )
//                .disableCache(
//                        CacheFlag.ACTIVITY
//                )
                .addEventListeners(new CommandListener())
//                .addEventListeners(new CatchListener())
                .addEventListeners(new ButtonListener())
                .addEventListeners(new ConnectionListener())
                .addEventListeners(new BGListener())
                .addEventListeners(new CountThreadListener())
                .addEventListeners(new PingHellListener())
                .addEventListeners(new PlaceListener())
                .addEventListeners(new BotDownDetectionListener())
                .addEventListeners(new ForumListener())
                .addEventListeners(new CountThread10kPolicingListener())
                .addEventListeners(new SlashCommandListener())
                .setActivity(Activity.playing("With Duckies"))
                .build();
    }
}
