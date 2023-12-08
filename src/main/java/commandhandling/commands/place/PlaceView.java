package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordhelpers.EmbedHelper;
import services.place.PlaceWebSocket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static services.discordhelpers.MessageDeleteHelper.deleteMsg;

public class PlaceView implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceView.class);

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Place").setImage("attachment://place.png");

        ctx.getChannel().sendMessageEmbeds(embed.build())
                .addFiles(FileUpload.fromData(convert(PlaceWebSocket.getImage(true)), "place.png")).queue(
                        msg -> {
                            if (!ctx.getPersist()) {
                                deleteMsg(msg, 64);
                            }
                        }
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

    @Override
    public String getName() {
        return "PlaceView";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns an image of the current place.");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pv");
    }

    public PlaceView(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }
}
