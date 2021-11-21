package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Miscellaneous;

import java.awt.*;
import java.io.File;

public class Shutdown implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Shutdown.class);

    public Shutdown(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);
        LOGGER.info(ctx.getSelfUser().getName() + "is shutting down");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Shutting down");
        embed.setColor(new Color(0xb074ad));
        embed.setImage("attachment://shutdown.gif");

        ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(new File("resources/shutdown.gif")).queue(
                msg -> Miscellaneous.deleteMsg(msg, 150)
        );
        ctx.getJDA().shutdown();
    }

    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Shuts down the JDA instance after pending rest actions have been executed");
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
