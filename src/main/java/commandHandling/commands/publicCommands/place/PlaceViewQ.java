package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

public class PlaceViewQ {
    private final CommandContext ctx;
    public PlaceViewQ(CommandContext ctx) {
        this.ctx = ctx;
        main();
    }

    private void main () {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Queue");
        String[] strs = DatabaseHandler.getCompletePlaceQ();

        if (strs[0].length() == 0) {
            embed.setDescription("There are no files in the queue");
        } else {
            embed.addField("__ID__", strs[0], true);
            embed.addField("__Drawn Pixels__", strs[1], true);
            embed.addField("__Queued by__", strs[2], true);
        }

        EmbedHelper.sendEmbed(ctx, embed, 64);
    }
}
