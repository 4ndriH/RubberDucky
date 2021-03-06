import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class main {
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA RubberDucky = JDABuilder.createDefault("ODE3ODQ2MDYxMzQ3MjQyMDI2.YEPcfw.84TqWsSqXQyCnes9MgdPdVeKj4U")
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.watching("development progress"))
                .build().awaitReady();
    }
}
