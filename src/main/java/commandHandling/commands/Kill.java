package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import resources.CONFIG;

import java.awt.*;
import java.io.File;

public class Kill implements CommandInterface {
    public Kill(Logger LOGGER) {
        LOGGER.info("Loaded Command Kill");
    }

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getAuthor().getId().equals(CONFIG.OwnerID.get())) {
            services.Logger.botStatus(ctx.getJDA(), ctx.getSelfUser().getName() + " is committing sudoku", "");
            services.Logger.command(ctx, "kill", true);
            ctx.getChannel().sendMessage("Shutting down").addFile(new File("resources/shutdown.gif")).queue();
            ctx.getJDA().shutdownNow();
        }
    }

    @Override
    public String getName() {
        return "kill";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Kill");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Shuts down the JDA instance and cancels all pending rest actions");
        return embed;
    }
}
