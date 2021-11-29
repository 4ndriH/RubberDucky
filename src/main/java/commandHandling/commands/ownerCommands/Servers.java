package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import resources.EMOTES;
import services.database.DatabaseHandler;
import services.Miscellaneous;

import java.awt.*;
import java.util.ArrayList;

public class Servers implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Servers.class);

    public Servers(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);

        try {
            if (ctx.getArguments().get(0).equals("this") && CONFIG.servers.contains(ctx.getGuild().getId())) {
                DatabaseHandler.removeServer(ctx.getGuild().getId());
            } else {
                DatabaseHandler.insertServer(ctx.getGuild().getId());
            }
            CONFIG.reload();
        } catch (Exception e) {
            ArrayList<String> ids = CONFIG.servers;
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("Whitelisted servers");
            embed.setColor(new Color(0xb074ad));

            if (ids.size() == 0) {
                embed.setDescription("-");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Guild guild : ctx.getJDA().getGuilds()) {
                    if (ids.contains(guild.getId())) {
                        sb.append(EMOTES.RDG.getAsEmote());
                    } else {
                        sb.append(EMOTES.RDR.getAsEmote());
                    }
                    sb.append(guild.getName()).append("\n");
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
    public boolean isOwnerOnly() {
        return true;
    }
}
