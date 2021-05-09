package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.GifSequenceWriter;
import services.PlaceWebSocket;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class preview implements Runnable{
    private final CommandContext ctx;

    public preview(CommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        BufferedImage place = PlaceWebSocket.getImage(false);
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        Scanner scanner;

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

        try {
            ImageOutputStream output = new FileImageOutputStream(new File("tempFiles/place/preview.gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 50, true);
            ArrayList<String[]> pixels = new ArrayList<>();

            while (scanner.hasNextLine()) {
                pixels.add(scanner.nextLine().split(" "));
            }

            int pixelsPerFrame = (int)(pixels.size() * 0.012);
            writer.writeToSequence(place);

            for (int i = 0; i < pixels.size(); i++) {
                img.setRGB(Integer.parseInt(pixels.get(i)[2]), Integer.parseInt(pixels.get(i)[3]), Color.decode(pixels.get(i)[4]).getRGB());
                if (i % pixelsPerFrame == 0) {
                    writer.writeToSequence(img);
                }
            }

            scanner.close();
            writer.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File gif = new File("tempFiles/place/preview.gif");
        ctx.getChannel().sendFile(gif).complete();
        gif.delete();
    }
}