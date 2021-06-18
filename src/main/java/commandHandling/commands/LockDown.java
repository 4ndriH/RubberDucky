package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import resources.CONFIG;

import java.util.List;

public class LockDown implements CommandInterface {
    public LockDown(Logger LOGGER) {
        LOGGER.info("Loaded Command Lockdown");
    }

    @Override
    public void handle(CommandContext ctx) {
        services.Logger.command(ctx, "lockdown", true);

        if (CONFIG.getChannels().size() == 0) {
            CONFIG.reload();
        } else {
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
