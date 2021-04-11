package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.database.dbHandlerQ;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class viewQ {
    private final CommandContext ctx;
    public viewQ(CommandContext ctx) {
        this.ctx = ctx;
        main();
    }

    private void main () {
        StringBuilder drawnPixels = new StringBuilder();
        StringBuilder users = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        EmbedBuilder embed = new EmbedBuilder();
        ResultSet rs = dbHandlerQ.getAll();

        try {
            while (rs.next()) {
                ids.append(rs.getString("key")).append("\n");
                drawnPixels.append(rs.getString("progress")).append("\n");
                users.append("<@!").append(rs.getString("user")).append(">\n");
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
            embed.addField("__ID__", ids.toString(), true);
            embed.addField("__Drawn Pixels__", drawnPixels.toString(), true);
            embed.addField("__Queued by__", users.toString(), true);
        }

        ctx.getChannel().sendMessage(embed.build()).queue(
                msg -> msg.delete().queueAfter(64, TimeUnit.SECONDS)
        );
    }
}
