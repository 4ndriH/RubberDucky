package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import services.BotExceptions;
import services.Miscellaneous;
import services.PermissionManager;
import services.database.DatabaseHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class PlaceDraw implements Runnable{
    private final CommandContext ctx;
    private final PlaceData placeData;

    public PlaceDraw(CommandContext ctx, PlaceData placeData) {
        this.ctx = ctx;
        this.placeData = placeData;
    }

    @Override
    public void run() {
        TextChannel placeBots = ctx.getJDA().getGuildById(747752542741725244L).getTextChannelById(819966095070330950L);
        Random random = new Random();

        if (ctx.getArguments().size() > 1 && PermissionManager.authenticateOwner(ctx)) {
            if (!DatabaseHandler.getPlaceQIDs().contains(placeData.id = Integer.parseInt(ctx.getArguments().get(1)))) {
                Miscellaneous.CommandLog("Place", ctx, false);
                BotExceptions.invalidIdException(ctx);
                return;
            }
        } else {
            ArrayList<Integer> numbers = DatabaseHandler.getPlaceQIDs();
            if (numbers.size() > 0) {
                placeData.id = numbers.get(random.nextInt(numbers.size()));
            } else {
                Miscellaneous.CommandLog("Place", ctx, false);
                BotExceptions.emptyQueueException(ctx);
                return;
            }
        }

        Miscellaneous.CommandLog("PlaceDraw", ctx, true);

        try {
            while (!placeData.stop && !placeData.stopQ) {
                String[] temp = DatabaseHandler.getPlaceQProject(placeData.id);
                placeData.file = temp[0];
                placeData.drawnPixels = Integer.parseInt(temp[1]);
                placeData.user = temp[2];
                placeData.drawing = true;
                Scanner scanner = new Scanner(new File("tempFiles/place/queue/" + placeData.file));
                ArrayList<String> pixels = new ArrayList<>();

                while (scanner.hasNextLine()) {
                    pixels.add(scanner.nextLine());
                }
                scanner.close();

                placeData.totalPixels = pixels.size();
                placeData.pixels = pixels;

                for (int i = placeData.drawnPixels; i < pixels.size() && !placeData.stop; i++) {
                    try {
                        placeBots.sendMessage(pixels.get(i)).complete();
                        placeData.drawnPixels++;
                        if (i % 16 == 0) {
                            placeData.updateProgress();
                            DatabaseHandler.updatePlaceQ(placeData.id, i);
                        }
                        if (placeData.verify && (i % 2048 == 0 && i != 0 || i == pixels.size() - 1)) {
                            new PlaceVerify(placeData);
                            while (placeData.verify && !placeData.fixingQ.isEmpty()) {
                                placeBots.sendMessage(placeData.fixingQ.pollFirst()).complete();
                                placeData.fixedPixels++;
                            }
                        }
                    } catch (Exception e) {
                        i--;
                        placeData.LOGGER.error("PlaceDraw Error", e);
                        Thread.sleep(16000);
                    }
                }

                if (!placeData.stop) {
                    sendCompletionMessage(Long.parseLong(placeData.user));
                    DatabaseHandler.removePlaceQ(placeData.id);
                    File myTxtObj = new File("tempFiles/place/queue/" + placeData.file);
                    while(myTxtObj.exists() && !myTxtObj.delete());
                    ArrayList<Integer> ids = DatabaseHandler.getPlaceQIDs();
                    if (ids.size() > 0) {
                        placeData.id = ids.get(random.nextInt(ids.size()));
                    } else {
                        return;
                    }
                }
            }
        } catch (FileNotFoundException | InterruptedException e) {
            placeData.LOGGER.error("PlaceDraw Error", e);
        }
        placeData.drawing = false;
    }

    private void sendCompletionMessage (long userID) {
        EmbedBuilder embed = Miscellaneous.embedBuilder("Your drawing has been finished");
        embed.setDescription("Thank you for using RubberDucky to draw");
        embed.setThumbnail("attachment://place.png");
        ctx.getJDA().openPrivateChannelById(userID).complete().sendMessageEmbeds(embed.build())
                .addFile(convert(services.PlaceWebSocket.getImage(true)), "place.png").queue();
    }

    private InputStream convert (BufferedImage img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(os.toByteArray());
    }
}
