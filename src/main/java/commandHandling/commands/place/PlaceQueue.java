package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.BotExceptions;
import services.DatabaseHandler;
import services.Miscellaneous;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class PlaceQueue {
    private final CommandContext ctx;
    private final PlaceData placeData;

    public PlaceQueue(CommandContext ctx, PlaceData placeData) {
        this.ctx = ctx;
        this.placeData = placeData;
        main();
    }

    private void main () {
        ArrayList<Integer> numbers = DatabaseHandler.getPlaceQIDs();
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
                Miscellaneous.CommandLog("Place", ctx, false);
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        Miscellaneous.CommandLog("Place", ctx, true);

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
            placeData.LOGGER.error("PlaceQueue Error", e);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Queue");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Your file got ID " + number);

        ctx.getMessage().replyEmbeds(embed.build()).queue(
                msg -> Miscellaneous.deleteMsg(msg, 32)
        );

        DatabaseHandler.insertPlaceQ(number, "RDdraw" + number + ".txt", ctx.getMessage().getAuthor().getId());
    }
}