package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.BotExceptions;
import services.database.dbHandlerQ;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

        if (ids.contains(id)) {
            File myTxtObj = new File("tempFiles/place/queue/RDdraw" + id + ".txt");
            dbHandlerQ.deleteElementInQ(id);
            while(myTxtObj.exists() && !myTxtObj.delete());
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Delete");
            embed.setColor(new Color(0xb074ad));
            embed.setDescription("File " + id + " has been deleted");
            ctx.getChannel().sendMessage(embed.build()).queue(msg ->
                    msg.delete().queueAfter(32, TimeUnit.SECONDS));
        } else {
            BotExceptions.fileDoesNotExistException(ctx);
        }
    }
}
