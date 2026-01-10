package commandhandling.commands.place;

import assets.Config;
import assets.objects.Pixel;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
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
    private final static Random RANDOM = new Random();

    @Override
    public void handle(CommandContext ctx) {
        if (Config.PLACE_PROJECT_ID == -1) {
            Config.updateConfig("placeProject", Integer.toString(-69));
            PLACE_INFINITE = true;
            placeInfinite(ctx);
        } else if (Config.PLACE_PROJECT_ID == -69) {
            Config.updateConfig("placeProject", Integer.toString(-1));
            PLACE_INFINITE = false;
        } else {
            LOGGER.warn("PlaceDraw is running, cannot start PlaceInfinite");
        }
    }

    private void placeInfinite(CommandContext ctx) {
        final TextChannel channel = Objects.requireNonNull(ctx.getJDA().getGuildById(SERVER_ID)).getTextChannelById(PLACE_CHANNEL_ID);
        final List<String> patterns = List.of("topdown", "diagonal", "spiral", "random", "circle", "spread", "lefttoright");

        assert channel != null;

        while (PLACE_INFINITE) {
            BufferedImage img = getRandomProfilePicture(ctx.getJDA());

            if (img == null) {
                PLACE_INFINITE = false;
                Config.updateConfig("placeProject", Integer.toString(-1));
                LOGGER.error("PlaceInfinite stopped due to error, image was null");
                return;
            }

            final int x = RANDOM.nextInt(1128) - 127;
            final int y = RANDOM.nextInt(1128) - 127;
//            final int width = RANDOM.nextInt(img.getWidth());
//            final int height = RANDOM.nextInt(img.getHeight());
            final String pattern = patterns.get(RANDOM.nextInt(patterns.size()));
            final boolean reverse = RANDOM.nextBoolean();

            if (img.getWidth() > 128 || img.getHeight() > 128) {
                img = PlaceEncode.resize(img, 128, 128);
            }

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
                try {
                    channel.sendMessage(p.getDrawCommand()).complete();
                } catch (final Exception e) {
                    LOGGER.debug("place infinite send error", e);
                }

                if (!PLACE_INFINITE) {
                    return;
                }
            }
        }
    }

    private BufferedImage getRandomProfilePicture(JDA jda) {
        final List<Member> members = Objects.requireNonNull(jda.getGuildById(SERVER_ID)).getMembers();
        Member m;
        String imageUrl;

        do {
            m = members.get((int) (Math.random() * members.size()));
            imageUrl = m.getUser().getAvatarUrl();
        } while (imageUrl == null);

        // if (imageUrl == null) {
        //     imageUrl = "https://cdn.discordapp.com/embed/avatars/" + RANDOM.nextInt(6) + ".png";
        // }

        // LOGGER.info("User: {} - {}", m.getUser().getName(), imageUrl);

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
