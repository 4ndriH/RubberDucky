package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;

import java.util.concurrent.ExecutionException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.*;
import java.awt.*;
import java.io.*;

public class preview {
    private final CommandContext ctx;

    public preview(CommandContext ctx) {
        this.ctx = ctx;
        previewing();
    }

    private void previewing() {
        Scanner scanner = null;
        BufferedImage img = null;

        try {
            img = ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()));
            scanner = new Scanner(ctx.getMessage().getReferencedMessage().getAttachments().get(0).retrieveInputStream().get());
        } catch (IOException | InterruptedException | ExecutionException e) {
            BotExceptions.missingAttachmentException(ctx);
            return;
        }

        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(i, j));
                int rgb = (int)(color.getRed() * 0.299);
                rgb += (color.getGreen() * 0.587) + (color.getBlue() * 0.114);
                img.setRGB(i, j, new Color(rgb, rgb, rgb).getRGB());
            }
        }

        while (scanner.hasNextLine()) {
            Scanner s = new Scanner(scanner.nextLine().substring(19));
            img.setRGB(s.nextInt(), s.nextInt(), Color.decode(s.next()).getRGB());
            s.close();
        }

        ctx.getChannel().sendMessage("").addFile(convert(img), "preview.png").queue();
    }

    private InputStream convert(BufferedImage img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(os.toByteArray());
    }
}
