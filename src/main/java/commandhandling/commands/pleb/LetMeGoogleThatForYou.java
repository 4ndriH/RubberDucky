package commandhandling.commands.pleb;

import assets.Config;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordhelpers.EmbedHelper;
import services.miscellaneous.GifSequenceWriter;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static services.discordhelpers.MessageSendHelper.sendMessageComplete;


public class LetMeGoogleThatForYou implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?!\\s*$).+\\s?");
    private static final Logger LOGGER = LoggerFactory.getLogger(LetMeGoogleThatForYou.class);
    private final Color txtC = new Color(0xe8eaed);

    @Override
    public void handle(CommandContext ctx) {
        String id = ctx.getMessage().getId();
        String input = ctx.getArguments().isEmpty() ? "lorem ipsum" : ctx.getArguments().stream().map(Object::toString).collect(Collectors.joining(" "));
        String searchURL = ctx.getArguments().isEmpty() ? "lorem+ipsum" : ctx.getArguments().stream().map(Object::toString).collect(Collectors.joining("+"));

        try {
            ImageOutputStream output = new FileImageOutputStream(new File(Config.DIRECTORY_PATH + "tempFiles/lmgtfy" + id + ".gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 80, true);

            BufferedImage lmgtfy = ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/lmgtfy.png"));
            BufferedImage lmgtfyResult = ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/lmgtfyResult.png"));
            BufferedImage temp = new BufferedImage(lmgtfy.getWidth(), lmgtfy.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Font font = new Font("Arial", Font.BOLD, 16);
            Color textColor = new Color(0xe8eaed);
            Color background = new Color(0x202124);
            Graphics2D g = temp.createGraphics();

            g.setFont(font);
            g.setColor(textColor);
            g.setBackground(new Color(69, 69, 69, 0));

            writer.writeToSequence(lmgtfy);
            writer.writeToSequence(ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/cursor0.png")));
            writer.writeToSequence(ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/cursor0.png")));
            writer.writeToSequence(ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/cursor0.png")));
            int offset = 0;

            for (int i = 1; i <= input.length(); i++) {
                if ((offset += rightMostPixel(temp)) < 0) {
                    g.setColor(background);
                    g.fillRect(460, 380, 508, 40);
                    g.setColor(textColor);
                    g.drawString(input.substring(0, i), 468 + offset, 404);
                    g.clearRect(0, 380, 469, 40);
                } else {
                    g.drawString(input.substring(0, i), 468 + offset, 404);
                }
                writer.writeToSequence(temp);
            }

            for (int i = 1; i < 10; i++) {
                writer.writeToSequence(ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/cursor" + i + ".png")));
            }

            writer.writeToSequence(ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/cursor10.png")));
            writer.writeToSequence(ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/cursor10.png")));
            writer.writeToSequence(ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/cursor10.png")));

            g = lmgtfyResult.createGraphics();
            g.setFont(font);
            g.setColor(textColor);
            g.drawString(input, 180, 54);

            g.drawImage(ImageIO.read(new File(Config.DIRECTORY_PATH + "resources/images/lmgtfy/lmgtfyFix.png")), null, 721, 26);

            for (int i = 0; i < 50; i++) {
                writer.writeToSequence(lmgtfyResult);
            }

            writer.close();
            output.close();
        } catch (IOException e) {
            LOGGER.error("Error creating lmgtfy gif", e);
        }

        File gif = new File(Config.DIRECTORY_PATH + "tempFiles/lmgtfy" + id + ".gif");

        EmbedBuilder embed = EmbedHelper.embedBuilder("Let Me Google That For You");
        embed.setDescription("[Google](https://www.google.com/search?q=" + searchURL + ")");
        embed.setImage("attachment://lmgtfy" + id + ".gif");

        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(gif));
        sendMessageComplete(mca, 1024);

        if (gif.delete()) {
            LOGGER.debug("Deleted file: " + gif.getName());
        } else {
            LOGGER.error("Failed to delete file: " + gif.getName());
        }
    }

    private int rightMostPixel(BufferedImage img) {
        for (int x = 1000; x >= 940; x--) {
            for (int y = 392; y <= 403; y++) {
                Color c = new Color(img.getRGB(x, y));
                if (c.getRed() == txtC.getRed() && c.getGreen() == txtC.getGreen() && c.getBlue() == txtC.getBlue()) {
                    return 940 - x;
                }
            }
        }
        return 0;
    }

    @Override
    public String getName() {
        return "LetMeGoogleThatForYou";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Did someone ask an easily googleable question? \nShow them how Google works");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("lmgtfy");
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
