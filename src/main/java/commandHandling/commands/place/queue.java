package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.StorageHandler;

import java.util.ArrayList;
import java.util.Random;

public class queue {
    private final CommandContext ctx;

    public queue(CommandContext ctx) {
        this.ctx = ctx;
        queueing();
    }

    private void queueing () {
        ArrayList<String> lines = StorageHandler.readData("resources/place/", "queue");
        ArrayList<Integer> numbers = new ArrayList<>();
        Random random = new Random();
        String file;
        int number;

        for (String s : lines) {
            numbers.add(Integer.parseInt(s.substring(s.length() - 4)));
        }

        do {
            number = random.nextInt(10000);
        } while (numbers.contains(number));

        file = "RDdraw" + number + ".txt";
        lines.add(file);

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

        StorageHandler.writeData("resources/place/", "queue", lines);
    }
}
