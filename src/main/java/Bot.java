import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import assets.CONFIG;
import services.listeners.*;
import services.onStartup.StartUp;

import javax.security.auth.login.LoginException;

public class Bot {
    public static void main(String[] args) throws LoginException {
        StartUp.actions();

        connectToDiscord();
    }

    private static void connectToDiscord() {
        JDABuilder.createDefault(CONFIG.token
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
            .addEventListeners(new CommandListener())
//                .addEventListeners(new CatchListener())
            .addEventListeners(new ButtonListener())
            .addEventListeners(new StartupListener())
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
