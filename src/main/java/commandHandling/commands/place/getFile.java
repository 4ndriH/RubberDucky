package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.database.dbHandlerQ;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class getFile {
    private final CommandContext ctx;

    public getFile(CommandContext ctx) {
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

        if (dbHandlerQ.getIDs().contains(id)) {
            ctx.getChannel().sendMessage("")
                    .addFile(new File("tempFiles/place/queue/" + dbHandlerQ.getFile(id))).queue(
                        msg -> msg.delete().queueAfter(64, TimeUnit.SECONDS)
            );
        } else {
            BotExceptions.fileDoesNotExistException(ctx);
        }
    }
}
