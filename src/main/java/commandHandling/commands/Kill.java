package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import services.DiscordLogger;

import java.awt.*;
import java.io.File;

public class Kill implements CommandInterface {
    public Kill(Logger LOGGER) {
        LOGGER.info("Loaded Command Kill");
    }

    @Override
    public void handle(CommandContext ctx) {
        DiscordLogger.command(ctx, "kill", true);
        DiscordLogger.botStatus(ctx.getJDA(), ctx.getSelfUser().getName() + " is committing sudoku", "");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Committing Sudoku");
        embed.setColor(new Color(0xb074ad));
        embed.setImage("attachment://sudoku.jpg");

        ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(new File("resources/sudoku.jpg")).complete();
        ctx.getJDA().shutdownNow();
    }

    @Override
    public String getName() {
        return "Kill";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Shuts down the JDA instance and cancels all pending rest actions");
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
