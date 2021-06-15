package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import resources.CONFIG;
import resources.EMOTES;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Servers implements CommandInterface {
    public Servers(Logger LOGGER) {
        LOGGER.info("Loaded Command Servers");
    }

    @Override
    public void handle(CommandContext ctx) {
        services.Logger.command(ctx, "servers", true);

        try {
            if (ctx.getArguments().get(0).equals("this") && CONFIG.getServers().contains(ctx.getGuild().getId())) {
                services.database.dbHandlerPermissions.removeFromServers(ctx.getGuild().getId());
            } else {
                services.database.dbHandlerPermissions.addToServers(ctx.getGuild().getId());
            }
            CONFIG.reload();
        } catch (Exception e) {
            ArrayList<String> ids = CONFIG.getServers();
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
                    msg -> msg.delete().queueAfter(32, TimeUnit.SECONDS)
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
        embed.setTitle("Help - Servers");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Add or remove servers from the whitelist");
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
