package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import resources.CONFIG;
import services.BotExceptions;

import java.awt.*;

public class Prefix implements CommandInterface {
    public Prefix(Logger LOGGER) {
        LOGGER.info("Loaded Command Prefix");
    }

    @Override
    public void handle(CommandContext ctx) {
        try {
            services.database.dbHandlerConfig.updateConfig("prefix", ctx.getArguments().get(0));
            services.Logger.command(ctx, "prefix", true);
            CONFIG.reload();
        } catch (Exception e) {
            services.Logger.command(ctx, "prefix", false);
            BotExceptions.invalidArgumentsException(ctx);
        }
    }

    @Override
    public String getName() {
        return "Prefix";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Prefix");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Change the prefix");
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
