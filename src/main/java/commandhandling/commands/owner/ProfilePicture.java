package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class ProfilePicture implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(ProfilePicture.class);

    @Override
    public void handle(CommandContext ctx) {
        try {
            Icon icon = Icon.from(convert(ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()))));
            ctx.getJDA().getSelfUser().getManager().setAvatar(icon).queue();
            LOGGER.info("Changed profile picture");
        } catch (Exception e) {
            LOGGER.error("Error while changing profile picture", e);
        }
    }

    private InputStream convert (BufferedImage img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            LOGGER.error("Error while converting image", e);
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
        return List.of("picture");
    }

    @Override
    public boolean attachmentCheck(CommandContext ctx) {
        if (ctx.getMessage().getAttachments().isEmpty()) {
            return false;
        }

        return Objects.requireNonNull(ctx.getMessage().getAttachments().get(0).getContentType()).startsWith("image");
    }
}
