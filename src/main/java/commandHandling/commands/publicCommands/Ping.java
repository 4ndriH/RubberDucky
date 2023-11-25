package commandHandling.commands.publicCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordHelpers.EmbedHelper;

public class Ping implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Ping.class);

    public Ping(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getJDA();

        LOGGER.warn("test");
        LOGGER.error("test");
        LOGGER.info("test");

        jda.getRestPing().queue(
                ping -> {
                    EmbedBuilder embed = EmbedHelper.embedBuilder("Ping");
                    embed.addField("__Discord Server Ping:__", ping + "ms", false);
                    embed.addField("__Discord Websocket Ping:__", jda.getGatewayPing() + "ms", false);
                    EmbedHelper.sendEmbed(ctx, embed, 32);
                }
        );
    }

    @Override
    public String getName() {
        return "Ping";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns the current ping");
        return embed;
    }
}
