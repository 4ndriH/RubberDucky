package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.DatabaseHandler;
import services.Miscellaneous;

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
            Miscellaneous.CommandLog("Place", ctx, false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (DatabaseHandler.getPlaceQIDs().contains(id)) {
            try {
                String[] strs = DatabaseHandler.getPlaceQProject(id);
                ctx.getChannel().sendFile(new File("tempFiles/place/queue/" + strs[0])).queue(
                            msg -> Miscellaneous.deleteMsg(msg, 64)
                );
                Miscellaneous.CommandLog("Place", ctx, true);
            } catch (IllegalArgumentException e) {
                Miscellaneous.CommandLog("Place", ctx, false);
                BotExceptions.FileExceedsUploadLimitException(ctx);
            }
        } else {
            Miscellaneous.CommandLog("Place", ctx, false);
            BotExceptions.fileDoesNotExistException(ctx);
        }
    }
}