package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.DatabaseHandler;
import services.DiscordLogger;
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
            DiscordLogger.command(ctx, "place", false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (DatabaseHandler.getPlaceQIDs().contains(id)) {
            try {
                String[] strs = DatabaseHandler.getPlaceQProject(id);
                ctx.getChannel().sendFile(new File("tempFiles/place/queue/" + strs[0])).queue(
                            msg -> Miscellaneous.deleteMsg(msg, 64)
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