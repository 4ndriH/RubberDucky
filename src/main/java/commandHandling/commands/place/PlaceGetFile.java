package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.Logger;
import services.database.dbHandlerQ;

import java.io.File;
import java.util.concurrent.TimeUnit;

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
            Logger.command(ctx, "place", false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (dbHandlerQ.getIDs().contains(id)) {
            try {
                ctx.getChannel().sendFile(new File("tempFiles/place/queue/" + dbHandlerQ.getFile(id))).queue(
                            msg -> msg.delete().queueAfter(64, TimeUnit.SECONDS)
                );
                Logger.command(ctx, "place", true);
            } catch (IllegalArgumentException e) {
                Logger.commandAndException(ctx, "place", e, false);
                BotExceptions.FileExceedsUploadLimitException(ctx);
            }
        } else {
            Logger.command(ctx, "place", false);
            BotExceptions.fileDoesNotExistException(ctx);
        }
    }
}