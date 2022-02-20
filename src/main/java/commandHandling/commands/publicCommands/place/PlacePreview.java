package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.GifSequenceWriter;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;
import services.place.PlaceWebSocket;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlacePreview implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlacePreview.class);

    public PlacePreview(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        BufferedImage place = PlaceWebSocket.getImage(false);
        Scanner scanner;

        if (ctx.getArguments().size() > 0) {
            int id;
            try {
                id = Integer.parseInt(ctx.getArguments().get(0));
            } catch (Exception e) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }

            if (DatabaseHandler.getPlaceProjectIDs().contains(id)) {
                try {
                    scanner = new Scanner(new File("tempFiles/place/queue/RDdraw" + id + ".txt"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                BotExceptions.fileDoesNotExistException(ctx);
                return;
            }
        } else {
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
        }

        try {
            ImageOutputStream output = new FileImageOutputStream(new File("tempFiles/place/preview.gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 50, true);
            ArrayList<String> pixels = new ArrayList<>();
            boolean exception = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.length() > 15) {
                    pixels.add(line.substring(16));
                } else {
                    pixels.add(line);
                }
            }
            scanner.close();

            int pixelsPerFrame = Math.max(1, (int)(pixels.size() * 0.005));
            writer.writeToSequence(place);

            for (int i = 0; i < pixels.size(); i++) {
                String[] pixel = pixels.get(i).split(" ");
                try {
                    img.setRGB(Integer.parseInt(pixel[0]), Integer.parseInt(pixel[1]), Color.decode(pixel[2]).getRGB());
                    if (i % pixelsPerFrame == 0) {
                        writer.writeToSequence(img);
                        img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
                    }
                } catch (Exception e) {
                    if (!exception) {
                        exception = true;
                        BotExceptions.faultyPixelFormatException(ctx, pixels.get(i));
                    }
                }
            }

            for (int i = 0; i < 64; i++) {
                writer.writeToSequence(img);
            }

            writer.close();
            output.close();
        } catch (Exception e) {
            LOGGER.error("PlacePreview Error", e);
        }

        File gif = new File("tempFiles/preview.gif");

        try {
            EmbedBuilder embed = EmbedHelper.embedBuilder("Preview");
            embed.setImage("attachment://preview.gif");
            ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(gif).queue(
                    msg -> EmbedHelper.deleteMsg(msg, 1024)
            );
        } catch (IllegalArgumentException e) {
            LOGGER.error("PlacePreview Error", e);
            BotExceptions.FileExceedsUploadLimitException(ctx);
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        gif.delete();
    }

    @Override
    public String getName() {
        return "PlacePreview";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pp");
    }
}
