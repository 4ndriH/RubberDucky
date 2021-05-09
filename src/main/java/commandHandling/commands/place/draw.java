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
        TextChannel ethPlaceBots = ctx.getJDA().getGuildById(747752542741725244L).getTextChannelById(819966095070330950L);
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
                placeData.pixels = pixels;

                for (int i = placeData.drawnPixels; i < pixels.size() && !placeData.stop; i++) {
                    try {
                        ethPlaceBots.sendMessage(pixels.get(i)).complete();
                        placeData.drawnPixels++;
                        if (i % 16 == 0) {
                            placeData.updateProgress();
                            dbHandlerQ.updateProgressInQ(i, placeData.id);
                        }
                        if (placeData.verify && i % 1800 == 0 && i != 0 || i == pixels.size() - 1) {
                            new verify(placeData);
                            while (placeData.verify && !placeData.fixingQ.isEmpty()) {
                                ethPlaceBots.sendMessage(placeData.fixingQ.pollFirst()).complete();
                            }
                        }
                    } catch (Exception e) {
                        i--;
                        System.out.println("Discord websocket/sending error caught!");
                        System.out.println(e);
                        Thread.sleep(8000);
                    }
                }

                if (!placeData.stop) {
                    sendCompletionMessage(Long.parseLong(dbHandlerQ.getUser(placeData.id)));
                    dbHandlerQ.deleteElementInQ(placeData.id);
                    File myTxtObj = new File("tempFiles/place/queue/" + file);
                    while(myTxtObj.exists() && !myTxtObj.delete());
                    ArrayList<Integer> ids = dbHandlerQ.getIDs();
                    if (ids.size() > 0) {
                        file = dbHandlerQ.getFile(placeData.id = ids.get(random.nextInt(ids.size())));
                    } else {
                        break;
                    }
                }
            }
        } catch (FileNotFoundException | InterruptedException e) {
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
