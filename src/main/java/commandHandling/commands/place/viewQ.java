package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.database.dbHandlerQ;

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

        if (rs == null) {
            ctx.getChannel().sendMessage("queue is empty").queue();
        } else {
            String dbContent = "Index, File, Progress";

            try {
                while (rs.next()) {
                    dbContent += "\n" + rs.getInt("key") + ", " + rs.getString("file")
                    + ", " + rs.getInt("progress");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            ctx.getChannel().sendMessage(dbContent).queue();
        }
    }
}
