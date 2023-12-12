package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.objects.Pixel;
import services.BotExceptions;
import services.PermissionManager;
import services.database.DBHandlerConfig;
import services.database.DBHandlerPlace;
import services.discordhelpers.EmbedHelper;
import assets.objects.PlaceData;
import services.place.PlaceWebSocket;
import services.place.PlaceVerifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PlaceDraw implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceDraw.class);
    private static boolean fixToggle = false;

    @Override
    public void handle(CommandContext ctx) {
        int id = -1;

        if (PlaceData.drawing) {
            return;
        } else if (ctx.getArguments().size() > 0 && PermissionManager.authenticateOwner(ctx)) {
            try {
                id = Integer.parseInt(ctx.getArguments().get(0));
                if (!DBHandlerPlace.getPlaceProjectIDs().contains(id)) {
                    BotExceptions.invalidIdException(ctx);
                    return;
                }
            } catch (Exception e) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }
        } else {
            id = DBHandlerPlace.getNextProject();
        }

        if (id == -1) {
            BotExceptions.emptyQueueException(ctx);
            return;
        }

        draw(ctx.getJDA(), id);
    }

    public static void draw(JDA jda, int id) {
        TextChannel placeChannel = jda.getGuildById(747752542741725244L).getTextChannelById(819966095070330950L);
        long time3600 = System.currentTimeMillis();
        int pixelDrawnCnt3600 = 0;

        while (!PlaceData.stopQ) {
            LOGGER.info("started drawing project: " + id);
            if (id < 0) {
                break;
            }

            new PlaceData(id);
            DBHandlerConfig.updateConfig("placeProject", "" + id);

            while (PlaceData.drawnPixels < PlaceData.totalPixels && !PlaceData.stop) {
                try {
                    if (PlaceData.verify && !PlaceData.fixingQ.isEmpty() && (fixToggle = !fixToggle)) {
                        placeChannel.sendMessage(PlaceData.fixingQ.poll().getDrawCommand()).complete();
                        PlaceData.fixedPixels++;
                    } else {
                        placeChannel.sendMessage(PlaceData.getPixel().getDrawCommand()).complete();
                        if (PlaceData.drawnPixels++ % 16 == 0) {
                            DBHandlerPlace.updateProgress(PlaceData.ID, PlaceData.drawnPixels);
                        }
                    }
//                    placeChannel.sendMessage(messageGenerator()).complete();

//                    if (PlaceData.drawnPixels++ % 16 == 0) {
//                        DBHandlerPlace.updateProgress(PlaceData.ID, PlaceData.drawnPixels);
//                    }
                } catch (Exception e) {
                    try {
                        Thread.sleep(16000);
                    } catch (InterruptedException ignored) {
                        LOGGER.error("caught an error in PlaceDraw");
                    }
                }

                if (PlaceData.verificationCondition()) {
                    LOGGER.info("does this potentially fuck things up?");
                    PlaceVerifier.verify();
                }

                if (++pixelDrawnCnt3600 == 3600) {
                    int tempSec = (int) ((System.currentTimeMillis() - time3600) / 1000);
                    DBHandlerPlace.insertTimeTaken(tempSec);
                    time3600 = System.currentTimeMillis();
                    pixelDrawnCnt3600 = 0;
                }
            }

            for (Pixel pixel : PlaceData.fixingQ) {
                LOGGER.info("finishing left over pixels");
                placeChannel.sendMessage(pixel.getDrawCommand()).complete();
                PlaceData.fixedPixels++;

                if (PlaceData.stop || !PlaceData.verify) {
                    LOGGER.info("BREAK");
                    break;
                }
            }

            if (!PlaceData.stop) {
                LOGGER.info("stop?");
                DBHandlerPlace.removeProjectFromQueue(PlaceData.ID);
                id = DBHandlerPlace.getNextProject();
                sendCompletionMessage(jda);
            } else {
                id = -1;
            }
        }

        DBHandlerConfig.updateConfig("placeProject", "-1");
        PlaceData.drawing = false;
        PlaceData.stopQ = false;
    }

    private static String messageGenerator() {
        StringBuilder command = new StringBuilder();

        command.append(drawOrContinue());

        if (PlaceData.totalPixels - PlaceData.drawnPixels > 128) {
            if (PlaceData.openPixelRequests()) {
                command.append(" | ").append(PlaceData.getPixelRequest());

                for (int i = 0; i < 60; i++) {
                    command.append(drawOrContinue());
                }
            }
        }

        return command.toString();
    }

    private static String drawOrContinue() {
        if (PlaceData.verify && !PlaceData.fixingQ.isEmpty() && (fixToggle = !fixToggle)) {
            PlaceData.fixedPixels++;
            return "/" + PlaceData.fixingQ.poll().getDrawCommand();
        } else {
            PlaceData.drawnPixels++;
            return "/" + PlaceData.getPixel().getDrawCommand();
        }
    }

    private static void sendCompletionMessage(JDA jda) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Your drawing has been finished");
        embed.setDescription("Thank you for using RubberDucky to draw");
        embed.setThumbnail("attachment://place.png");
        jda.openPrivateChannelById(PlaceData.user).complete().sendMessageEmbeds(embed.build())
                .addFiles(FileUpload.fromData(convert(PlaceWebSocket.getImage(true)), "place.png")).queue();
    }

    private static InputStream convert(BufferedImage img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(os.toByteArray());
    }

    @Override
    public String getName() {
        return "PlaceDraw";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Starts the drawing process\n" +
                "Anyone can start the queue, which gets executed in ascending order");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pd");
    }
}
