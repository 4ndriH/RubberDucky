package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.PermissionManager;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;
import services.place.PlaceData;
import services.place.PlaceVerify;
import services.place.PlaceWebSocket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PlaceDraw implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceDraw.class);

    public PlaceDraw(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel placeChannel = ctx.getJDA().getGuildById(747752542741725244L).getTextChannelById(955751651942211604L);
        int id = -1;

        if (PlaceData.drawing) {
            return;
        }else if (ctx.getArguments().size() > 0 && PermissionManager.authenticateOwner(ctx)) {
            try {
                id = Integer.parseInt(ctx.getArguments().get(0));
                if (!DatabaseHandler.getPlaceProjectIDs().contains(id)) {
                    BotExceptions.invalidIdException(ctx);
                    return;
                }
            } catch (Exception e) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }
        }


        while (!PlaceData.stop && !PlaceData.stopQ) {
            id = (id == -1) ? DatabaseHandler.getLowestPlaceProjectID() : id;

            if (id < 0) {
                BotExceptions.emptyQueueException(ctx);
                DatabaseHandler.updateConfig("placeProject", "-1");
                return;
            }

            String[] project = DatabaseHandler.getPlaceProject(id);
            new PlaceData(id, Integer.parseInt(project[0]), project[1]);
            DatabaseHandler.updateConfig("placeProject", "" + id);
            boolean fixToggle = false;

            while (PlaceData.drawnPixels < PlaceData.totalPixels && !PlaceData.stop) {
                try {
                    if (!PlaceData.fixingQ.isEmpty() && (fixToggle = !fixToggle)) {
                        placeChannel.sendMessage(".place setpixel " + PlaceData.fixingQ.poll()).complete();
                        PlaceData.fixedPixels++;
                    } else {
                        placeChannel.sendMessage(".place setpixel " + PlaceData.pixels.get(PlaceData.drawnPixels)).complete();

                        if (++PlaceData.drawnPixels % 16 == 0) {
                            DatabaseHandler.updateProgress(PlaceData.ID, PlaceData.drawnPixels);
                        }
                    }
                } catch (Exception e) {
                    try {
                        Thread.sleep(16000);
                    } catch (InterruptedException ignored) {}
                }

                if (PlaceData.verify && PlaceData.fixingQ.isEmpty() && PlaceData.drawnPixels % 2000 == 0) {
                    PlaceVerify.verify();
                }
            }

            sendCompletionMessage(ctx);
        }
    }

    private void sendCompletionMessage (CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Your drawing has been finished");
        embed.setDescription("Thank you for using RubberDucky to draw");
        embed.setThumbnail("attachment://place.png");
        ctx.getJDA().openPrivateChannelById(PlaceData.ID).complete().sendMessageEmbeds(embed.build())
                .addFile(convert(PlaceWebSocket.getImage(true)), "place.png").queue();
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

    @Override
    public String getName() {
        return "PlaceDraw";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pd");
    }
}
