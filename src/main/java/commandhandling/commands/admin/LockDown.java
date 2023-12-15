package commandhandling.commands.admin;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.PermissionManager;

import java.util.List;

import static services.PermissionManager.getWhitelistedChannels;

public class LockDown implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(LockDown.class);

    @Override
    public void handle(CommandContext ctx) {
        if (getWhitelistedChannels().size() == 0) {
            PermissionManager.reload();
        } else {
            LOGGER.info("Initiating Lockdown!");
            PermissionManager.initiateLockdown();
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
    public int getRestrictionLevel() {
        return 1;
    }
}
