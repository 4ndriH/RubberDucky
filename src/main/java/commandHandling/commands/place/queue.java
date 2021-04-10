package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import services.BotExceptions;
import services.PermissionManager;
import services.database.dbHandlerQ;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class queue {
    private final CommandContext ctx;

    public queue(CommandContext ctx) {
        this.ctx = ctx;
        queueing();
    }

    private void queueing () {
        ArrayList<Integer> numbers = dbHandlerQ.getIDs();
        ArrayList<String> commands = new ArrayList<>();
        Random random = new Random();
        Scanner scanner;
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
            scanner = new Scanner(ctx.getMessage().getAttachments().get(0).retrieveInputStream().get());
        } catch (Exception e) {
            try {
                scanner = new Scanner(ctx.getMessage().getReferencedMessage().getAttachments().get(0)
                        .retrieveInputStream().get());
            } catch (Exception ee) {
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        while (scanner.hasNextLine())
            commands.add(scanner.nextLine());

        if (!PermissionManager.authOwner(ctx) && commands.size() > 10000) {
            BotExceptions.fileTooBigException(ctx);
        } else {
            try {
                PrintStream printer = new PrintStream("tempFiles/place/queue/" + file);
                for (String s : commands)
                    printer.println(s);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Queue");
            embed.setColor(new Color(0xb074ad));
            embed.setDescription("Your file got ID " + number);

            dbHandlerQ.addToQ(number, file, ctx.getMessage().getAuthor().getId());
            ctx.getMessage().reply(embed.build()).queue(msg ->  {
                msg.delete().queueAfter(32, TimeUnit.SECONDS);
            });
        }

        ctx.getMessage().delete().queueAfter(32, TimeUnit.SECONDS);
    }
}
