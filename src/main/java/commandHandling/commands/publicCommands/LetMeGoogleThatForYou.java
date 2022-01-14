package commandHandling.commands.publicCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.GifSequenceWriter;
import services.logging.EmbedHelper;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class LetMeGoogleThatForYou implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(LetMeGoogleThatForYou.class);

    public LetMeGoogleThatForYou(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        String input = ctx.getArguments().size() == 0 ? "lorem ipsum" : ctx.getArguments().stream().map(Object::toString).collect(Collectors.joining(" "));
        String searchURL = ctx.getArguments().stream().map(Object::toString).collect(Collectors.joining("+"));

        try {
            ImageOutputStream output = new FileImageOutputStream(new File("tempFiles/lmgtfy.gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 80, true);

            BufferedImage lmgtfy = ImageIO.read(new File("resources/lmgtfy/lmgtfy.png"));
            BufferedImage lmgtfyResult = ImageIO.read(new File("resources/lmgtfy/lmgtfyResult.png"));
            BufferedImage temp = new BufferedImage(lmgtfy.getWidth(), lmgtfy.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Font font = new Font("Arial", Font.BOLD, 16);
            Color textColor = new Color(0xe8eaed);
            Graphics g = temp.getGraphics();

            g.setFont(font);
            g.setColor(textColor);

            writer.writeToSequence(lmgtfy);
            writer.writeToSequence(ImageIO.read(new File("resources/lmgtfy/cursor0.png")));
            writer.writeToSequence(ImageIO.read(new File("resources/lmgtfy/cursor0.png")));
            writer.writeToSequence(ImageIO.read(new File("resources/lmgtfy/cursor0.png")));

            for (int i = 1; i <= input.length(); i++) {
                g.drawString(input.substring(0, i), 465, 401);
                writer.writeToSequence(temp);
            }

            for (int i = 1; i < 10; i++) {
                writer.writeToSequence(ImageIO.read(new File("resources/lmgtfy/cursor" + 1 + ".png")));
            }

            writer.writeToSequence(ImageIO.read(new File("resources/lmgtfy/cursor10.png")));
            writer.writeToSequence(ImageIO.read(new File("resources/lmgtfy/cursor10.png")));
            writer.writeToSequence(ImageIO.read(new File("resources/lmgtfy/cursor10.png")));

            g = lmgtfyResult.getGraphics();
            g.setFont(font);
            g.setColor(textColor);
            g.drawString(input, 180, 54);

            for (int i = 0; i < 50; i++) {
                writer.writeToSequence(lmgtfyResult);
            }

            writer.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File gif = new File("tempFiles/lmgtfy.gif");

        EmbedBuilder embed = EmbedHelper.embedBuilder("Let Me Google That For You");
        embed.setDescription("[Google](https://www.google.com/search?q=" + searchURL + ")");
        embed.setImage("attachment://lmgtfy.gif");

        ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(gif).queue(
                msg -> EmbedHelper.deleteMsg(msg, 1024)
        );

        gif.delete();
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
}
