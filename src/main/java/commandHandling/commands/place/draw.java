package commandHandling.commands.place;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import commandHandling.CommandContext;
import services.BotExceptions;
import services.PermissionManager;
import services.database.dbHandlerQ;

import java.awt.*;
import java.util.*;
import java.io.*;

public class draw implements Runnable{
    private final CommandContext ctx;
    private final placeData placeData;

    public draw(CommandContext ctx, placeData placeData) {
        this.ctx = ctx;
        this.placeData = placeData;
    }

    @Override
    public void run() {
        TextChannel ethPlaceBots = ctx.getGuild().getTextChannelById(819966095070330950L);
        Random random = new Random();
        String file;

        if (ctx.getArguments().size() > 1 && PermissionManager.authOwner(ctx)) {
            file = dbHandlerQ.getFile(placeData.setID(Integer.parseInt(ctx.getArguments().get(1))));
            if (file.length() == 0) {
                BotExceptions.invalidIdException(ctx);
                return;
            }
        } else {
            ArrayList<Integer> numbers = dbHandlerQ.getIDs();
            if (numbers.size() > 0) {
                file = dbHandlerQ.getFile(placeData.setID(Integer.parseInt(ctx.getArguments().get(1))));
            } else {
                BotExceptions.emptyQueueException(ctx);
                return;
            }
        }

        try {
            while (file != null && !placeData.stop() && !placeData.stopQ()) {
                placeData.setDrawing(true);
                Scanner scanner = new Scanner(new File("tempFiles/place/queue/" + file));
                ArrayList<String> pixels = new ArrayList<>();
                int start = dbHandlerQ.getProgress(placeData.getID());

                while (scanner.hasNextLine()) {
                    pixels.add(scanner.nextLine());
                }
                scanner.close();

                placeData.setTotalPixels(pixels.size());

                for (int i = start; i < pixels.size() && !placeData.stop(); i++) {
                    ethPlaceBots.sendMessage(pixels.get(i)).complete();
                    placeData.setDrawnPixels(i);
                    if (i % 16 == 0) {
                        dbHandlerQ.updateProgressInQ(i, placeData.getID());
                    }
                }

                if (!placeData.stop()) {
                    sendCompletionMessage();
                    dbHandlerQ.deleteElementInQ(placeData.getID());
                    File myObj = new File("tempFiles/place/queue/" + file);
                    myObj.delete();
                    ArrayList<Integer> numbers = dbHandlerQ.getIDs();
                    if (numbers.size() > 0) {
                        file = dbHandlerQ.getFile(placeData.setID(Integer.parseInt(ctx.getArguments().get(1))));
                    } else {
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            placeData.setDrawing(false);
            e.printStackTrace();
        }
        placeData.setDrawing(false);
    }

    private void sendCompletionMessage () {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Your drawing has been finished");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Thank you for using RubberDucky to draw");
        ctx.getMessage().getAuthor().openPrivateChannel().complete().sendMessage(embed.build()).queue();
    }
}
