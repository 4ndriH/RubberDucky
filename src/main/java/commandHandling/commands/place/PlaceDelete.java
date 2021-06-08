package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.BotExceptions;
import services.Logger;
import services.database.dbHandlerQ;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaceDelete {
    private final CommandContext ctx;

    public PlaceDelete(CommandContext ctx) {
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
                Logger.command(ctx, "place", false);
                BotExceptions.fileDoesNotExistException(ctx);
                return;
            }
        } catch (Exception e) {
            Logger.commandAndException(ctx, "place", e, false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        Logger.command(ctx, "place", true);

        embed.setTitle("Delete");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("File " + id + " has been deleted");
        ctx.getChannel().sendMessage(embed.build()).queue(
                msg -> msg.delete().queueAfter(32, TimeUnit.SECONDS)
        );
    }
}
