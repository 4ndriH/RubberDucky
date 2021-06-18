package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import resources.CONFIG;

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
            if (CONFIG.getBlackList().contains(ctx.getArguments().get(0))) {
                services.database.dbHandlerPermissions.removeFromBlackList(ctx.getArguments().get(0));
            } else {
                services.database.dbHandlerPermissions.addToBlackList(ctx.getArguments().get(0));
            }
            CONFIG.reload();
        } catch (Exception e) {
            ArrayList<String> ids = CONFIG.getBlackList();
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("Blacklisted people");
            embed.setColor(new Color(0xb074ad));

            if (ids.size() == 0) {
                embed.setDescription("-");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String id : ids) {
                    sb.append("<@!").append(id).append(">\n");
                }
                Message msg = ctx.getChannel().sendMessage("beep boop").complete();
                msg.editMessage(sb.toString()).complete();
                msg.delete().queue();
                embed.setDescription(sb.toString());
            }

            ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
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
        embed.setDescription("Allows me to blacklist your annoying ass");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("bl");
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}