package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.BotExceptions;
import services.Miscellaneous;
import services.database.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;

public class PlaceDelete {
    private final CommandContext ctx;

    public PlaceDelete(CommandContext ctx) {
        this.ctx = ctx;
        main();
    }

    private void main() {
        ArrayList<Integer> ids = DatabaseHandler.getPlaceQIDs();
        EmbedBuilder embed = Miscellaneous.embedBuilder("Delete");
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(1));
            if (ids.contains(id)) {
                File myTxtObj = new File("tempFiles/place/queue/RDdraw" + id + ".txt");
                DatabaseHandler.removePlaceQ(id);
                while(myTxtObj.exists() && !myTxtObj.delete());
            } else {
                Miscellaneous.CommandLog("Place", ctx, false);
                BotExceptions.fileDoesNotExistException(ctx);
                return;
            }
        } catch (Exception e) {
            Miscellaneous.CommandLog("Place", ctx, false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        Miscellaneous.CommandLog("Place", ctx, true);

        embed.setDescription("File " + id + " has been deleted");
        ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                msg -> Miscellaneous.deleteMsg(msg, 32)
        );
    }
}
