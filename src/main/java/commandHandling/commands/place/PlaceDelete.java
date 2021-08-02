package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.BotExceptions;
import services.DatabaseHandler;
import services.DiscordLogger;
import services.Miscellaneous;

import java.awt.*;
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
        EmbedBuilder embed = new EmbedBuilder();
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(1));
            if (ids.contains(id)) {
                File myTxtObj = new File("tempFiles/place/queue/RDdraw" + id + ".txt");
                DatabaseHandler.removePlaceQ(id);
                while(myTxtObj.exists() && !myTxtObj.delete());
            } else {
                DiscordLogger.command(ctx, "place", false);
                BotExceptions.fileDoesNotExistException(ctx);
                return;
            }
        } catch (Exception e) {
            DiscordLogger.commandAndException(ctx, "place", e, false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        DiscordLogger.command(ctx, "place", true);

        embed.setTitle("Delete");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("File " + id + " has been deleted");
        ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                msg -> Miscellaneous.deleteMsg(msg, 32)
        );
    }
}
