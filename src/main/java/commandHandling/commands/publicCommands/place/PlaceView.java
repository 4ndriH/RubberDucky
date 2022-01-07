package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.logging.EmbedHelper;
import services.PlaceWebSocket;

import javax.imageio.ImageIO;
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
        EmbedBuilder embed = EmbedHelper.embedBuilder("Place").setImage("attachment://place.png");

        ctx.getChannel().sendMessageEmbeds(embed.build())
                .addFile(convert(PlaceWebSocket.getImage(true)), "place.png").queue(
                msg -> EmbedHelper.deleteMsg(msg, 64)
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
