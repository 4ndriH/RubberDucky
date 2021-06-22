package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.DiscordLogger;
import services.Miscellaneous;
import services.database.dbHandlerQ;

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
            DiscordLogger.command(ctx, "place", false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (dbHandlerQ.getIDs().contains(id)) {
            try {
                ctx.getChannel().sendFile(new File("tempFiles/place/queue/" + dbHandlerQ.getFile(id))).queue(
                            msg -> Miscellaneous.deleteMsg(ctx, msg, 64)
                );
                DiscordLogger.command(ctx, "place", true);
            } catch (IllegalArgumentException e) {
                DiscordLogger.commandAndException(ctx, "place", e, false);
                BotExceptions.FileExceedsUploadLimitException(ctx);
            }
        } else {
            DiscordLogger.command(ctx, "place", false);
            BotExceptions.fileDoesNotExistException(ctx);
        }
    }
}