package commandHandling.commands.placeCommands;

import assets.CONFIG;
import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.database.DBHandlerConfig;
import assets.Objects.PlaceData;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PlaceVerify implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceVerify.class);

    public PlaceVerify(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getMessage().getReferencedMessage() == null) {
            PlaceData.verify = !PlaceData.verify;
            DBHandlerConfig.updateConfig("PlaceVerify", "" + PlaceData.verify);
            CONFIG.reload();
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
    public List<String> getAliases() {
        return List.of("pv");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
