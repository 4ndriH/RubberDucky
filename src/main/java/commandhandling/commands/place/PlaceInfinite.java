package commandhandling.commands.place;

import assets.Config;
import assets.objects.Pixel;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.place.patterns.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.Random;


public class PlaceInfinite implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceInfinite.class);

    private static final String SERVER_ID = "747752542741725244";
    private static final String PLACE_CHANNEL_ID = "819966095070330950";

    private static boolean PLACE_INFINITE = false;

    @Override
    public void handle(CommandContext ctx) {
        if (Config.PLACE_PROJECT_ID == -1) {
            Config.updateConfig("placeProject", Integer.toString(-69));
            PLACE_INFINITE = true;
            LOGGER.info("PlaceInfinite started");
            placeInfinite(ctx);
        } else if (Config.PLACE_PROJECT_ID == -69) {
            Config.updateConfig("placeProject", Integer.toString(-1));
            PLACE_INFINITE = false;
            LOGGER.info("PlaceInfinite stopped");
        } else {
            LOGGER.warn("PlaceDraw is running, cannot start PlaceInfinite");
        }
    }

    private void placeInfinite(CommandContext ctx) {
        final TextChannel channel = Objects.requireNonNull(ctx.getJDA().getGuildById(SERVER_ID)).getTextChannelById(PLACE_CHANNEL_ID);
        final List<String> patterns = List.of("topdown", "diagonal", "spiral", "random", "circle", "spread", "lefttoright");

        assert channel != null;

        while (PLACE_INFINITE) {
            final Random random = new Random();

            BufferedImage img = getRandomProfilePicture(ctx.getJDA());

            if (img == null) {
                PLACE_INFINITE = false;
                Config.updateConfig("placeProject", Integer.toString(-69));
                LOGGER.error("PlaceInfinite stopped due to error, image was null");
                return;
            }

            final int x = random.nextInt(1000);
            final int y = random.nextInt(1000);
//            final int width = random.nextInt(img.getWidth());
//            final int height = random.nextInt(img.getHeight());
            final String pattern = patterns.get(random.nextInt(patterns.size()));
            final boolean reverse = random.nextBoolean();

//            img = PlaceEncode.resize(img, width, height);

            List<Pixel> pixels;

            switch (pattern) {
                case "topdown", "td" -> pixels = TopDown.encode(img, x, y);
                case "diagonal"      -> pixels = Diagonal.encode(img, x, y);
                case "spiral"        -> pixels = Spiral.encode(img, x, y);
                case "random"        -> pixels = services.place.patterns.Random.encode(img, x, y);
                case "circle"        -> pixels = Circle.encode(img, x, y);
                case "spread"        -> pixels = Spread.encode(img, x, y, false, new ArrayList<>());
                default              -> pixels = LeftToRight.encode(img, x, y);
            }

            if (reverse) {
                Collections.reverse(pixels);
            }

            for (Pixel p : pixels)     {
                channel.sendMessage(p.getDrawCommand()).complete();

                if (!PLACE_INFINITE) {
                    return;
                }
            }
        }
    }

    private BufferedImage getRandomProfilePicture(JDA jda) {
        List<Member> members = Objects.requireNonNull(jda.getGuildById(SERVER_ID)).getMembers();

        Member m = members.get((int) (Math.random() * members.size()));
        String imageUrl = m.getUser().getAvatarUrl();

        try {
            return ImageIO.read(new URL(imageUrl));
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public String getName() {
        return "PlaceInfinite";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Should draw forever.");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pi");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return true;
    }
}
