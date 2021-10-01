package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.Miscellaneous;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ProfilePicture implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(ProfilePicture.class);

    public ProfilePicture(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        try {
            Icon icon = Icon.from(convert(ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()))));
            ctx.getJDA().getSelfUser().getManager().setAvatar(icon).queue();
            Miscellaneous.CommandLog(getName(), ctx, true);
        } catch (Exception e) {
            Miscellaneous.CommandLog(getName(), ctx, false);
            BotExceptions.missingAttachmentException(ctx);
        }
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
        return "Profilepicture";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Changes the bots profile picture");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("picture", "pp");
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
