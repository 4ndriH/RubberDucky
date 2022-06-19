package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.EMOTES;
import services.PermissionManager;
import services.database.DBHandlerWhitelistedServers;
import services.EmbedHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Servers implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Servers.class);

    public Servers(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getArguments().size() > 0) {
            String argument = ctx.getArguments().get(0);

            if (argument.equals("this")) {
                argument = ctx.getGuild().getId();
            }

            if (PermissionManager.servers.contains(argument)) {
                DBHandlerWhitelistedServers.removeServerFromWhitelist(ctx.getGuild().getId());
            } else {
                DBHandlerWhitelistedServers.addServerToWhitelist(ctx.getGuild().getId());
            }

            PermissionManager.reload();
        } else {
            EmbedBuilder embed = EmbedHelper.embedBuilder("Whitelisted servers");
            ArrayList<String> ids = PermissionManager.servers;
            HashMap<String, String> servers = new HashMap<>();
            ArrayList<String> names = new ArrayList<>();
            StringBuilder sb = new StringBuilder();

            for (Guild guild : ctx.getJDA().getGuilds()) {
                String name = guild.getName();
                names.add(name);

                if (ids.contains(guild.getId())) {
                    servers.put(name, EMOTES.RDG.getAsEmote() + " " + name);
                } else {
                    servers.put(name, EMOTES.RDR.getAsEmote() + " " + name);
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
    public int getRestrictionLevel() {
        return 0;
    }
}
