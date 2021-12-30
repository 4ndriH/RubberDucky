package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.BotExceptions;
import services.database.DatabaseHandler;
import services.Miscellaneous;

public class Prefix implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Prefix.class);

    public Prefix(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        try {
            DatabaseHandler.updateConfig("prefix", ctx.getArguments().get(0));
            Miscellaneous.CommandLog(getName(), ctx, true);
            CONFIG.reload();
        } catch (Exception e) {
            Miscellaneous.CommandLog(getName(), ctx, false);
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
        embed.setDescription("Changes the prefix");
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}