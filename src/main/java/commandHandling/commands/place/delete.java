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
        main();
    }

    private void main() {
        ArrayList<Integer> ids = dbHandlerQ.getIDs();
        EmbedBuilder embed = new EmbedBuilder();
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(1));
            if (ids.contains(id)) {
                File myTxtObj = new File("tempFiles/place/queue/RDdraw" + id + ".txt");
                dbHandlerQ.deleteElementInQ(id);
                while(myTxtObj.exists() && !myTxtObj.delete());
            } else {
                BotExceptions.fileDoesNotExistException(ctx);
            }
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        embed.setTitle("Delete");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("File " + id + " has been deleted");
        ctx.getChannel().sendMessage(embed.build()).queue(
                msg -> msg.delete().queueAfter(32, TimeUnit.SECONDS)
        );
    }
}
