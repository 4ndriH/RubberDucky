package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import services.BotExceptions;
import services.PermissionManager;
import services.database.dbHandlerQ;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
            file = dbHandlerQ.getFile(placeData.id = Integer.parseInt(ctx.getArguments().get(1)));
            if (file.length() == 0) {
                BotExceptions.invalidIdException(ctx);
                return;
            }
        } else {
            ArrayList<Integer> numbers = dbHandlerQ.getIDs();
            if (numbers.size() > 0) {
                file = dbHandlerQ.getFile(placeData.id = numbers.get(random.nextInt(numbers.size())));
            } else {
                BotExceptions.emptyQueueException(ctx);
                return;
            }
        }

        try {
            while (file != null && !placeData.stop && !placeData.stopQ) {
                placeData.drawing = true;
                Scanner scanner = new Scanner(new File("tempFiles/place/queue/" + file));
                ArrayList<String> pixels = new ArrayList<>();
                placeData.drawnPixels = dbHandlerQ.getProgress(placeData.id);

                while (scanner.hasNextLine()) {
                    pixels.add(scanner.nextLine());
                }
                scanner.close();

                placeData.totalPixels = pixels.size();

                if (placeData.drawnPixels != 0) {
                    for (int i = 0; i < placeData.drawnPixels; i++) {
                        placeData.setPixel(pixels.get(i));
                    }
                }

                for (int i = placeData.drawnPixels; i < pixels.size() && !placeData.stop; i++) {
                    ethPlaceBots.sendMessage(pixels.get(i)).complete();
                    placeData.setPixel(pixels.get(i));
                    if (i % 16 == 0) {
                        dbHandlerQ.updateProgressInQ(i, placeData.id);
                    }
                    if (i % 1800 == 0 || i == pixels.size() - 1) {
                        new verify(placeData);
                        while (!placeData.fixingQ.isEmpty()) {
                            ethPlaceBots.sendMessage(placeData.fixingQ.pollFirst()).complete();
                        }
                    }
                }

                if (!placeData.stop) {
                    sendCompletionMessage(Long.parseLong(dbHandlerQ.getUser(placeData.id)));
                    dbHandlerQ.deleteElementInQ(placeData.id);
                    File myTxtObj = new File("tempFiles/place/queue/" + file);
                    while(myTxtObj.exists() && !myTxtObj.delete());
                    ArrayList<Integer> numbers = dbHandlerQ.getIDs();
                    if (numbers.size() > 0) {
                        file = dbHandlerQ.getFile(placeData.id = Integer.parseInt(ctx.getArguments().get(1)));
                    } else {
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        placeData.drawing = false;
    }

    private void sendCompletionMessage (long userID) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Your drawing has been finished");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Thank you for using RubberDucky to draw");
        ctx.getJDA().openPrivateChannelById(userID).complete().sendMessage(embed.build()).queue();
    }
}
