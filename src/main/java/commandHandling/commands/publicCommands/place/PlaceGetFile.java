package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

import java.io.File;

public class PlaceGetFile {
    private final CommandContext ctx;

    public PlaceGetFile(CommandContext ctx) {
        this.ctx = ctx;
        main();
    }

    private void main () {
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(1));
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (DatabaseHandler.getPlaceQIDs().contains(id)) {
            try {
                String[] strs = DatabaseHandler.getPlaceQProject(id);
                ctx.getChannel().sendFile(new File("tempFiles/place/queue/" + strs[0])).queue(
                            msg -> EmbedHelper.deleteMsg(msg, 64)
                );
            } catch (IllegalArgumentException e) {
                BotExceptions.FileExceedsUploadLimitException(ctx);
            }
        } else {
            BotExceptions.fileDoesNotExistException(ctx);
        }
    }
}