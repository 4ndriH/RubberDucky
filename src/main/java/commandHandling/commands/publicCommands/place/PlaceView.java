package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.Miscellaneous;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PlaceView implements Runnable{
    private final CommandContext ctx;

    public PlaceView(CommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        Miscellaneous.CommandLog("Place", ctx, true);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Place");
        embed.setColor(new Color(0xb074ad));
        embed.setImage("attachment://place.png");

        ctx.getChannel().sendMessageEmbeds(embed.build())
                .addFile(convert(services.PlaceWebSocket.getImage(true)), "place.png").queue(
                msg -> Miscellaneous.deleteMsg(msg, 64)
        );
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
