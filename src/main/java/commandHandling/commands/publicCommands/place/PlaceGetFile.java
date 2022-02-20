package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

import java.io.File;
import java.util.List;

public class PlaceGetFile implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceGetFile.class);

    public PlaceGetFile(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(0));
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (DatabaseHandler.getPlaceProjectIDs().contains(id)) {
            try {
                ctx.getChannel().sendFile(new File("tempFiles/place/queue/RDdraw" + id + ".txt")).queue(
                        msg -> EmbedHelper.deleteMsg(msg, 64)
                );
            } catch (IllegalArgumentException e) {
                BotExceptions.FileExceedsUploadLimitException(ctx);
            }
        } else {
            BotExceptions.fileDoesNotExistException(ctx);
        }
    }

    @Override
    public String getName() {
        return "PlaceGetFile";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pgf");
    }
}
