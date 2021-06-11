package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import resources.CONFIG;

import java.awt.*;
import java.io.File;

public class Shutdown implements CommandInterface {
    public Shutdown(Logger LOGGER) {
        LOGGER.info("Loaded Command Shutdown");
    }

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getAuthor().getId().equals(CONFIG.OwnerID.get())) {
            services.Logger.command(ctx, "shutdown", true);
            services.Logger.botStatus(ctx.getJDA(), ctx.getSelfUser().getName() + " is shutting down",
                    "Waiting for buckets to finish");

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Shutting down");
            embed.setColor(new Color(0xb074ad));
            embed.setImage("attachment://shutdown.gif");

            ctx.getChannel().sendMessage(embed.build()).addFile(new File("resources/shutdown.gif")).queue();
            ctx.getJDA().shutdown();
        }
    }

    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Shutdown");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Shuts down the JDA instance but lets pending rest actions execute");
        return embed;
    }
}
