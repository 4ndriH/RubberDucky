package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.database.dbHandlerQ;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class queue {
    private final CommandContext ctx;

    public queue(CommandContext ctx) {
        this.ctx = ctx;
        queueing();
    }

    private void queueing () {
        ArrayList<Integer> numbers = dbHandlerQ.getIDs();
        Random random = new Random();
        String file;
        int number;

        do {
            number = random.nextInt(10000);
        } while (numbers.contains(number));

        if (ctx.getArguments().size() > 1) {
            file = ctx.getArguments().get(1) + ".txt";
        } else {
            file = "RDdraw" + number + ".txt";
        }

        try {
            ctx.getMessage().getAttachments().get(0).downloadToFile("tempFiles/place/queue/" + file);
        } catch (Exception e) {
            try {
                ctx.getMessage().getReferencedMessage().getAttachments().get(0)
                        .downloadToFile("tempFiles/place/queue/" + file);
            } catch (Exception ee) {
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        ctx.getMessage().reply("Your file got id: " + number).queue(msg ->  {
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
        dbHandlerQ.addToQ(number, file, ctx.getMessage().getAuthor().getId());
        ctx.getMessage().delete().queueAfter(32, TimeUnit.SECONDS);
    }
}
