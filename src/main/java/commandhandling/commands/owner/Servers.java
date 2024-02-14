package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Emotes;
import services.PermissionManager;
import services.database.DBHandlerWhitelistedServers;
import services.discordhelpers.EmbedHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

import static services.PermissionManager.getWhitelistedServers;


public class Servers implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Servers.class);
    public static final Pattern argumentPattern = Pattern.compile("^(?:this\\s?)?$");

    @Override
    public void handle(CommandContext ctx) {
        ArrayList<String> serverIds = getWhitelistedServers();

        if (!ctx.getArguments().isEmpty() ) {
            String serverId = ctx.getGuild().getId();

            if (serverIds.contains(serverId)) {
                DBHandlerWhitelistedServers.removeServerFromWhitelist(serverId);
            } else {
                DBHandlerWhitelistedServers.addServerToWhitelist(serverId);
            }

            PermissionManager.reload();
        } else {
            EmbedBuilder embed = EmbedHelper.embedBuilder("Whitelisted servers");
            HashMap<String, String> servers = new HashMap<>();
            ArrayList<String> names = new ArrayList<>();
            StringBuilder sb = new StringBuilder();

            for (Guild guild : ctx.getJDA().getGuilds()) {
                String name = guild.getName();
                names.add(name);

                if (serverIds.contains(guild.getId())) {
                    servers.put(name, Emotes.RDG.getAsEmote() + " " + name);
                } else {
                    servers.put(name, Emotes.RDR.getAsEmote() + " " + name);
                }
            }

            Collections.sort(names);

            for (String s : names) {
                sb.append(servers.get(s)).append("\n");
            }

            embed.setDescription(sb.toString());
            EmbedHelper.sendEmbed(ctx, embed, 64);
        }
    }

    @Override
    public String getName() {
        return "Servers";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Adds or removes a server from the whitelist");
        return embed;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
