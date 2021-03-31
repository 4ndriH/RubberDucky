package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.database.dbHandlerQ;

import java.io.File;
import java.util.ArrayList;

public class delete {
    private final CommandContext ctx;

    public delete(CommandContext ctx) {
        this.ctx = ctx;
        deleting();
    }

    private void deleting() {
        ArrayList<Integer> ids = dbHandlerQ.getIDs();
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(1));
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (ids.size() == 0) {
            ctx.getChannel().sendMessage("Queue is empty").queue();
        } else if (ids.contains(id)) {
            File myTxtObj = new File("tempFiles/place/queue/RDdraw" + id + ".txt");
            dbHandlerQ.deleteElementInQ(id);
            while(myTxtObj.exists() && !myTxtObj.delete());
        } else {
            ctx.getChannel().sendMessage("This file does not exist").queue();
        }
    }
}
