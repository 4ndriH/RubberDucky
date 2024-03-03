package commandhandling.commands.place;

import assets.Config;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import assets.objects.PlaceData;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class PlaceVerify implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceVerify.class);
    private static final List<String> types = List.of("jpg", "jpeg", "png");

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getMessage().getReferencedMessage() == null) {
            PlaceData.verify = !PlaceData.verify;
            Config.updateConfig("PlaceVerify", "" + PlaceData.verify);
        } else {
            try {
                PlaceData.addPlaceImageManually(ImageIO.read(new URL(ctx.getMessage().getReferencedMessage().getAttachments().get(0).getUrl())));
                LOGGER.debug("Added place image manually");
            } catch (IOException e) {
                BotExceptions.missingAttachmentException(ctx);
                return;
            }

            PlaceData.verify = true;
        }
    }

    @Override
    public String getName() {
        return "PlaceVerify";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Toggles whether or not placed pixels are getting verified");
        return embed;
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }

    @Override
    public boolean attachmentCheck(CommandContext ctx) {
        if (!ctx.getMessage().getAttachments().isEmpty()) {
            String type = Objects.requireNonNull(ctx.getMessage().getAttachments().get(0).getContentType()).split("/")[1];

            return types.contains(type);
        }
        return true;
    }
}
