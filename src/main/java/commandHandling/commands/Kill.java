package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Miscellaneous;

import java.awt.*;
import java.io.File;

public class Kill implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Kill.class);

    public Kill(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);
        LOGGER.info(ctx.getSelfUser().getName() + " is committing sudoku");

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
