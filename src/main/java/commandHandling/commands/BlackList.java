package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import resources.CONFIG;
import services.DatabaseHandler;
import services.DiscordLogger;
import services.Miscellaneous;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlackList implements CommandInterface {
    public BlackList(Logger LOGGER) {
        LOGGER.info("Loaded Command BlackList");
    }

    @Override
    public void handle(CommandContext ctx) {
        DiscordLogger.command(ctx, "blacklist", true);

        try {
            String id = ctx.getArguments().get(0).replace("<@!", "").replace(">", "");
            if (CONFIG.getBlackList().contains(id)) {
                DatabaseHandler.removeBlacklist(id);
            } else {
                DatabaseHandler.insertBlacklist(id);
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
                    msg -> Miscellaneous.deleteMsg(msg, 32)
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