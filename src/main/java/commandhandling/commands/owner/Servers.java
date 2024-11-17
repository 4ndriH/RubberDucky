package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Emotes;
import services.PermissionManager;
import services.database.DBHandlerWhitelistedServers;
import services.discordhelpers.EmbedHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

import static services.PermissionManager.getWhitelistedServers;
import static services.discordhelpers.MessageSendHelper.sendMessage;


public class Servers implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:this\\s?)?\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(Servers.class);

    @Override
    public void handle(CommandContext ctx) {
        ArrayList<String> serverIds = getWhitelistedServers();

        if (!ctx.getArguments().isEmpty() ) {
            String serverId = ctx.getGuild().getId();

            if (serverIds.contains(serverId)) {
                DBHandlerWhitelistedServers.removeServerFromWhitelist(serverId);
                LOGGER.info("Removed server " + Objects.requireNonNull(ctx.getJDA().getGuildById(serverId)).getName() + " from the whitelist");
            } else {
                DBHandlerWhitelistedServers.addServerToWhitelist(serverId);
                LOGGER.info("Added server " + Objects.requireNonNull(ctx.getJDA().getGuildById(serverId)).getName() + " the the whitelist");
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
            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
            sendMessage(ctx, mca, 64);
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
