package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.BotExceptions;
import services.Logger;
import services.database.dbHandlerQ;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PlaceQueue {
    private final CommandContext ctx;

    public PlaceQueue(CommandContext ctx) {
        this.ctx = ctx;
        main();
    }

    private void main () {
        ArrayList<Integer> numbers = dbHandlerQ.getIDs();
        ArrayList<String> commands = new ArrayList<>();
        Random random = new Random();
        Scanner scanner;
        int number;

        do {
            number = random.nextInt(10000);
        } while (numbers.contains(number));

        try {
            scanner = new Scanner(ctx.getMessage().getAttachments().get(0).retrieveInputStream().get());
        } catch (Exception e) {
            try {
                scanner = new Scanner(ctx.getMessage().getReferencedMessage().getAttachments().get(0)
                        .retrieveInputStream().get());
            } catch (Exception ee) {
                Logger.command(ctx, "place", false);
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        Logger.command(ctx, "place", true);

        while (scanner.hasNextLine()) {
            commands.add(scanner.nextLine());
        }
        scanner.close();

        try {
            PrintStream printer = new PrintStream("tempFiles/place/queue/" + "RDdraw" + number + ".txt");
            for (String s : commands) {
                printer.println(s);
            }
            printer.close();
        } catch (FileNotFoundException e) {
            Logger.exception(ctx, e);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Queue");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Your file got ID " + number);

        ctx.getMessage().replyEmbeds(embed.build()).queue(
                msg -> msg.delete().queueAfter(32, TimeUnit.SECONDS)
        );

        dbHandlerQ.addToQ(number, "RDdraw" + number + ".txt", ctx.getMessage().getAuthor().getId());
    }
}