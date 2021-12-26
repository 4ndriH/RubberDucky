package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.Miscellaneous;
import services.database.DatabaseHandler;

public class PlaceViewQ {
    private final CommandContext ctx;
    public PlaceViewQ(CommandContext ctx) {
        this.ctx = ctx;
        main();
    }

    private void main () {
        EmbedBuilder embed = Miscellaneous.embedBuilder("Queue");
        String[] strs = DatabaseHandler.getCompletePlaceQ();

        Miscellaneous.CommandLog("Place", ctx, true);

        if (strs[0].length() == 0) {
            embed.setDescription("There are no files in the queue");
        } else {
            embed.addField("__ID__", strs[0], true);
            embed.addField("__Drawn Pixels__", strs[1], true);
            embed.addField("__Queued by__", strs[2], true);
        }

        ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                msg -> Miscellaneous.deleteMsg(msg, 64)
        );
    }
}
