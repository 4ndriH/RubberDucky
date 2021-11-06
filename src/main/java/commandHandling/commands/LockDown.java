package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.Miscellaneous;

import java.util.List;

public class LockDown implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(LockDown.class);

    public LockDown(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);

        if (CONFIG.channels.size() == 0) {
            CONFIG.reload();
        } else {
            LOGGER.info("Initiating Lockdown!");
            CONFIG.initiateLockdown();
        }
    }

    @Override
    public String getName() {
        return "Lockdown";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Prevents anyone from using this bot. Except me of course");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ld");
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
