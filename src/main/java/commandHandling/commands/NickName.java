package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;

import java.awt.*;
import java.util.List;

public class NickName implements CommandInterface {
    public NickName(Logger LOGGER) {
        LOGGER.info("Loaded Command Nickname");
    }

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();

        for (String s : ctx.getArguments()) {
            sb.append(s).append(" ");
        }

        ctx.getSelfMember().modifyNickname(sb.toString()).queue();
        services.Logger.command(ctx, "nickname", true);
    }

    @Override
    public String getName() {
        return "Nickname";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Nickname");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Change the bots nickname");
        embed.addField("Aliases", "```rdnn```", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("nn");
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
