package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Ping implements CommandInterface {
    public Ping(Logger LOGGER) {
        LOGGER.info("Loaded Command Ping");
    }

    @Override
    public void handle(CommandContext ctx) {
        services.Logger.command(ctx, "ping", true);
        JDA jda = ctx.getJDA();

        jda.getRestPing().queue(
                ping -> {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Ping");
                    embed.setColor(new Color(0xb074ad));
                    embed.addField("__Discord Server Ping:__", ping + "ms", false);
                    embed.addField("__Discord Websocket Ping:__", jda.getGatewayPing() + "ms", false);
                    ctx.getChannel().sendMessageEmbeds(embed.build()).queue(msg ->
                            msg.delete().queueAfter(32, TimeUnit.SECONDS));
                }
        );
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns the current ping");
        return embed;
    }
}
