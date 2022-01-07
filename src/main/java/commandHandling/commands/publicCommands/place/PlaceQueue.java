package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.BotExceptions;
import services.CommandManager;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

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
                CommandManager.commandLogger("Place", ctx, false);
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        CommandManager.commandLogger("Place", ctx, true);

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

        EmbedBuilder embed = EmbedHelper.embedBuilder("Queue");
        embed.setDescription("Your file got ID " + number);

        EmbedHelper.sendEmbed(ctx, embed, 32);

        DatabaseHandler.insertPlaceQ(number, "RDdraw" + number + ".txt", ctx.getMessage().getAuthor().getId());
    }
}