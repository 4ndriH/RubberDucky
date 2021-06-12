package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BlackList implements CommandInterface {
    public BlackList(Logger LOGGER) {
        LOGGER.info("Loaded Command BlackList");
    }

    @Override
    public void handle(CommandContext ctx) {
        services.Logger.command(ctx, "blacklist", true);
        try {
            services.database.dbHandlerPermissions.blacklist(ctx.getArguments().get(0));
        } catch (Exception e) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Blacklisted people");
            embed.setColor(new Color(0xb074ad));
            ArrayList<String> ids = services.database.dbHandlerPermissions.getBlacklist();

            if (ids.size() == 0) {
                embed.setDescription("-");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String s : ids) {
                    sb.append("<@!").append(s).append(">\n");
                }
                Message msg = ctx.getChannel().sendMessage("beep boop").complete();
                msg.editMessage(sb.toString()).complete();
                msg.delete().queue();
                embed.setDescription(sb.toString());
            }

            ctx.getChannel().sendMessage(embed.build()).queue(
                    msg -> msg.delete().queueAfter(32, TimeUnit.SECONDS)
            );
        }
    }

    @Override
    public String getName() {
        return "Blacklist";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Blacklist");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Allows me to blacklist your annoying ass");
        embed.addField("Aliases", "```rdbl```", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("bl");
    }
}