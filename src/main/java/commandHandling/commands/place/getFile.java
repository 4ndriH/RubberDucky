package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.database.dbHandlerQ;

import java.io.File;
import java.util.ArrayList;

public class getFile {
    private final CommandContext ctx;

    public getFile(CommandContext ctx) {
        this.ctx = ctx;
        gettingFile();
    }

    private void gettingFile () {
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(1));
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        ArrayList<Integer> ids = dbHandlerQ.getIDs();
        if (!ids.contains(id)) {
            BotExceptions.fileDoesNotExistException(ctx);
            return;
        }

        String file = dbHandlerQ.getByID(id);

        ctx.getChannel().sendMessage(file + ":")
                .addFile(new File("tempFiles/place/queue/" + file)).queue();
    }
}
