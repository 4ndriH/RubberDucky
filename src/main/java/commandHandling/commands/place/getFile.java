package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.database.dbHandlerQ;

import java.io.File;

public class getFile {
    private final CommandContext ctx;

    public getFile(CommandContext ctx) {
        this.ctx = ctx;
    }

    private void gettingFile () {
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(1));
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        String file = dbHandlerQ.getByID(id);

        ctx.getChannel().sendMessage(file + ":")
                .addFile(new File("tempFiles/place/queue/" + file)).queue();
    }
}
