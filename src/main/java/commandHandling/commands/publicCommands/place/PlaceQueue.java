package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.PermissionManager;

import java.util.List;

public class PlaceQueue implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceQueue.class);

    public PlaceQueue(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        int id;
        if (ctx.getArguments().size() == 1 && ctx.perm)
    }

    @Override
    public String getName() {
        return "PlaceQueue";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("q");
    }
}
