package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;
import java.util.regex.Pattern;

public class PlaceDraw implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:10000|[1-9][0-9]{0,3}|0)?\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceDraw.class);

    @Override
    public void handle(CommandContext ctx) {
        if (!PlaceData.drawing) {
            int id = DBHandlerPlace.getNextProject();

            if (!ctx.getArguments().isEmpty() && PermissionManager.authenticateOwner(ctx)) {
                id = Integer.parseInt(ctx.getArguments().get(0));
                if (!DBHandlerPlace.getPlaceProjectIDs().contains(id)) {
                    BotExceptions.invalidIdException(ctx);
                    return;
                }
            }

            if (id == -1) {
                BotExceptions.emptyQueueException(ctx);
                return;
            }

            draw(ctx.getJDA(), id);
        }
    }

    public static void draw(JDA jda, int projectId) {
        TextChannel placeChannel = Objects.requireNonNull(jda.getGuildById(747752542741725244L)).getTextChannelById(819966095070330950L);
        long time3600 = System.currentTimeMillis();
        int pixelDrawnCnt3600 = 0;

        assert placeChannel != null;

        while (!PlaceData.stopQ && projectId >= 0) {
            LOGGER.debug("started drawing project: " + projectId);
            DBHandlerConfig.updateConfig("placeProject", "" + projectId);
            PlaceData.newProject(projectId);

            while (!PlaceData.stop && PlaceData.pixelsLeftToDraw()) {
                try {
                    if (PlaceData.verify && !PlaceData.fixingQ.isEmpty()) {
                        placeChannel.sendMessage(PlaceData.fixingQ.poll().getDrawCommand()).complete();
                        PlaceData.fixedPixels++;
                        pixelDrawnCnt3600++;
                    }

                    if (PlaceData.drawnPixels < PlaceData.totalPixels) {
                        placeChannel.sendMessage(PlaceData.getPixel().getDrawCommand()).complete();
                        PlaceData.drawnPixels++;
                        pixelDrawnCnt3600++;
                    }
                } catch (ErrorResponseException e) {
                    ErrorResponse er = e.getErrorResponse();
                    LOGGER.warn("PlaceDraw ErrorResponse: " + er.getMeaning(), e);
                    napTime();
                } catch (Exception e) {
                    LOGGER.warn("caught an error in PlaceDraw", e);
                    napTime();
                }

                if (PlaceData.drawnPixels % 16 == 0) {
                    DBHandlerPlace.updateProgress(PlaceData.ID, PlaceData.drawnPixels);
                }

                if (PlaceData.verificationCondition()) {
                    LOGGER.debug("Verify");
                    PlaceVerifier.verify();
                    LOGGER.debug(PlaceData.fixingQ.size() + " pixels left to fix");

                    if (PlaceData.drawnPixels == PlaceData.totalPixels) {
                        PlaceData.finalVerification = true;
                    }
                }

                if (pixelDrawnCnt3600 == 3600) {
                    int tempSec = (int) ((System.currentTimeMillis() - time3600) / 1000);
                    DBHandlerPlace.insertTimeTaken(tempSec);
                    time3600 = System.currentTimeMillis();
                    pixelDrawnCnt3600 = 0;
                }
            }

            if (!PlaceData.stop) {
                LOGGER.debug("stopping project: " + projectId);
                DBHandlerPlace.removeProjectFromQueue(PlaceData.ID);
                projectId = DBHandlerPlace.getNextProject();
                sendCompletionMessage(jda);
            } else {
                projectId = -1;
            }
        }

        DBHandlerConfig.updateConfig("placeProject", "-1");
        PlaceData.drawing = false;
        PlaceData.stopQ = false;
    }

//    private static String messageGenerator() {
//        StringBuilder command = new StringBuilder();
//
//        command.append(drawOrContinue());
//
//        if (PlaceData.totalPixels - PlaceData.drawnPixels > 128) {
//            if (PlaceData.openPixelRequests()) {
//                command.append(" | ").append(PlaceData.getPixelRequest());
//
//                for (int i = 0; i < 60; i++) {
//                    command.append(drawOrContinue());
//                }
//            }
//        }
//
//        return command.toString();
//    }

//    private static String drawOrContinue() {
//        if (PlaceData.verify && !PlaceData.fixingQ.isEmpty() && (fixToggle = !fixToggle)) {
//            PlaceData.fixedPixels++;
//            return "/" + PlaceData.fixingQ.poll().getDrawCommand();
//        } else {
//            PlaceData.drawnPixels++;
//            return "/" + PlaceData.getPixel().getDrawCommand();
//        }
//    }

    private static void napTime() {
        try {
            Thread.sleep(16000);
        } catch (InterruptedException ignored) {}
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
            LOGGER.error("Error converting image", e);
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

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }

}
