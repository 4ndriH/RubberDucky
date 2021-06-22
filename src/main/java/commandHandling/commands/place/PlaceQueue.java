package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.BotExceptions;
import services.DiscordLogger;
import services.Miscellaneous;
import services.database.dbHandlerQ;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
                DiscordLogger.command(ctx, "place", false);
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        DiscordLogger.command(ctx, "place", true);

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
            DiscordLogger.exception(ctx, e);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Queue");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Your file got ID " + number);

        ctx.getMessage().replyEmbeds(embed.build()).queue(
                msg -> Miscellaneous.deleteMsg(ctx, msg, 32)
        );

        dbHandlerQ.addToQ(number, "RDdraw" + number + ".txt", ctx.getMessage().getAuthor().getId());
    }
}