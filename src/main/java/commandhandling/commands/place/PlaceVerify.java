package commandhandling.commands.place;

import assets.Config;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.database.DBHandlerConfig;
import assets.objects.PlaceData;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PlaceVerify implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceVerify.class);

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getMessage().getReferencedMessage() == null) {
            PlaceData.verify = !PlaceData.verify;
            DBHandlerConfig.updateConfig("PlaceVerify", "" + PlaceData.verify);
            Config.reload();
        } else {
            try {
                PlaceData.addPlaceImageManually(ImageIO.read(new URL(ctx.getMessage().getReferencedMessage().getAttachments().get(0).getUrl())));
            } catch (IOException e) {
                BotExceptions.missingAttachmentException(ctx);
                return;
            }

            PlaceData.verify = true;
        }
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
}
