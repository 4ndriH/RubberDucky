package commandHandling.commands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.PermissionManager;
import services.database.DBHandlerConfig;
import services.database.DBHandlerPlace;
import services.discordHelpers.EmbedHelper;
import services.place.PlaceData;
import services.place.PlaceWebSocket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PlaceHyperDraw implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceHyperDraw.class);

    public PlaceHyperDraw(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

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
        TextChannel placeChannel = jda.getGuildById("747752542741725244").getTextChannelById("819966095070330950");

        while (!PlaceData.stopQ) {
            if (id < 0) {
                break;
            }

            new PlaceData(id);
            PlaceData.addThreads(jda.getGuildById("747752542741725244").getThreadChannels());

            DBHandlerConfig.updateConfig("placeProject", "" + id);
            boolean fixToggle = false;
            int threadIterator = 0;

            while (PlaceData.drawnPixels < PlaceData.totalPixels && !PlaceData.stop) {
                try {
//                    if (PlaceData.verify && !PlaceData.fixingQ.isEmpty() && (fixToggle = !fixToggle)) {
//                        placeChannel.sendMessage(PlaceData.fixingQ.poll().getDrawCommand()).complete();
//                        PlaceData.fixedPixels++;
//                    } else {
                    if (PlaceData.threads.isEmpty() || PlaceData.threads.size() == threadIterator) {
                        placeChannel.sendMessage(PlaceData.getPixel().getDrawCommand()).complete();
                        threadIterator = 0;
                    } else {
                        PlaceData.threads.get(threadIterator++).sendMessage(PlaceData.getPixel().getDrawCommand()).complete();
                    }

                    if (PlaceData.drawnPixels++ % 16 == 0) {
                        DBHandlerPlace.updateProgress(PlaceData.ID, PlaceData.drawnPixels);
                    }
//                    }
                } catch (Exception e) {
                    try {
                        Thread.sleep(16000);
                    } catch (InterruptedException ignored) {}
                }

//                if (PlaceData.verify && PlaceData.fixingQ.isEmpty() && PlaceData.drawnPixels % 2000 == 0
//                        || PlaceData.drawnPixels == PlaceData.totalPixels) {
//                    PlaceVerify.verify();
//                }
            }

//            for (Pixel pixel : PlaceData.fixingQ) {
//                placeChannel.sendMessage(pixel.getDrawCommand()).complete();
//                PlaceData.fixedPixels++;
//            }

            if (!PlaceData.stop) {
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

    private static void sendCompletionMessage(JDA jda) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Your drawing has been finished");
        embed.setDescription("Thank you for using RubberDucky to draw");
        embed.setThumbnail("attachment://place.png");
        jda.openPrivateChannelById(PlaceData.user).complete().sendMessageEmbeds(embed.build())
                .addFile(convert(PlaceWebSocket.getImage(true)), "place.png").queue();
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
        return "PlaceHyperDraw";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Starts the hyper drawing process");
        return embed;
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }

    @Override
    public List<String> getAliases() {
        return List.of("phd");
    }
}
