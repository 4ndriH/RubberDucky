package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.DatabaseHandler;
import services.Miscellaneous;

import java.awt.*;

public class PlaceViewQ {
    private final CommandContext ctx;
    public PlaceViewQ(CommandContext ctx) {
        this.ctx = ctx;
        main();
    }

    private void main () {
        EmbedBuilder embed = new EmbedBuilder();
        String[] strs = DatabaseHandler.getCompletePlaceQ();

        Miscellaneous.CommandLog("PlaceViewQ", ctx, true);

        embed.setTitle("Queue");
        embed.setColor(new Color(0xb074ad));

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
