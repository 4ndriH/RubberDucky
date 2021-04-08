package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.database.dbHandlerQ;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class viewQ {
    private final CommandContext ctx;
    public viewQ(CommandContext ctx) {
        this.ctx = ctx;
        viewingQ();
    }

    private void viewingQ () {
        ResultSet rs = dbHandlerQ.getAll();

        EmbedBuilder embed = new EmbedBuilder();
        String ids = "", users = "", drawnPixels = "";

        try {
            while (rs.next()) {
                ids += rs.getString("key") + "\n";
                drawnPixels += rs.getString("progress") + "\n";
                users += "<@!" + rs.getString("user") + ">\n";
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return;
        }

        embed.setTitle("Queue");
        embed.setColor(new Color(0xb074ad));

        if (ids.length() == 0) {
            embed.setDescription("There are no files in the queue");
        } else {
            embed.addField("__ID__", ids, true);
            embed.addField("__Drawn Pixels__", drawnPixels, true);
            embed.addField("__Queued by__", users, true);
        }

        ctx.getChannel().sendMessage(embed.build()).queue();
    }
}
