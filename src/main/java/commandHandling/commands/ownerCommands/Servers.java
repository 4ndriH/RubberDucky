package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.EMOTES;
import services.Miscellaneous;
import services.PermissionManager;
import services.database.DatabaseHandler;

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
        Miscellaneous.CommandLog(getName(), ctx, true);

        try {
            if (ctx.getArguments().get(0).equals("this") && PermissionManager.servers.contains(ctx.getGuild().getId())) {
                DatabaseHandler.removeServer(ctx.getGuild().getId());
            } else {
                DatabaseHandler.insertServer(ctx.getGuild().getId());
            }
            PermissionManager.reload();
        } catch (Exception e) {
            ArrayList<String> ids = PermissionManager.servers;
            EmbedBuilder embed = Miscellaneous.embedBuilder("Whitelisted servers");

            if (ids.size() == 0) {
                embed.setDescription("-");
            } else {
                HashMap<String, String> servers = new HashMap<>();
                ArrayList<String> names = new ArrayList<>();
                StringBuilder sb = new StringBuilder();
                String name;

                for (Guild guild : ctx.getJDA().getGuilds()) {
                    names.add(name = guild.getName());

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
            }

            ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                    msg -> Miscellaneous.deleteMsg(msg, 32)
            );
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
