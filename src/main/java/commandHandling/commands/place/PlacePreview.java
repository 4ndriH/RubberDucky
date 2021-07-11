package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.*;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PlacePreview implements Runnable{
    private final CommandContext ctx;

    public PlacePreview(CommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        BufferedImage place = PlaceWebSocket.getImage(false);
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        boolean exception = false;
        Scanner scanner;

        try {
            scanner = new Scanner(ctx.getMessage().getAttachments().get(0).retrieveInputStream().get());
        } catch (Exception e) {
            try {
                scanner = new Scanner(ctx.getMessage().getReferencedMessage().getAttachments().get(0)
                        .retrieveInputStream().get());
            } catch (Exception ee) {
                DiscordLogger.command(ctx, "place", false);
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        DiscordLogger.command(ctx, "place", true);

        try {
            ImageOutputStream output = new FileImageOutputStream(new File("tempFiles/place/preview.gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 50, true);
            ArrayList<String> pixels = new ArrayList<>();

            while (scanner.hasNextLine()) {
                pixels.add(scanner.nextLine());
            }
            scanner.close();

            int pixelsPerFrame = Math.max(1, (int)(pixels.size() * 0.012));
            writer.writeToSequence(place);

            for (int i = 0; i < pixels.size(); i++) {
                String[] pixel = pixels.get(i).split(" ");
                try {
                    img.setRGB(Integer.parseInt(pixel[2]), Integer.parseInt(pixel[3]), Color.decode(pixel[4]).getRGB());
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

            for (int i = 0; i < 8; i++) {
                writer.writeToSequence(img);
            }

            writer.close();
            output.close();
        } catch (Exception e) {
            DiscordLogger.exception(ctx, e);
        }

        File gif = new File("tempFiles/place/preview.gif");

        try {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Preview");
            embed.setColor(new Color(0xb074ad));
            embed.setImage("attachment://preview.gif");
            ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(gif).queue(
                    msg -> Miscellaneous.deleteMsg(ctx, msg, 1024)
            );
        } catch (IllegalArgumentException e) {
            DiscordLogger.exception(ctx, e);
            BotExceptions.FileExceedsUploadLimitException(ctx);
        }

        gif.delete();
    }
}