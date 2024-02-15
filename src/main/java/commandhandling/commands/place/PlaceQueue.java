package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.objects.Pixel;
import services.BotExceptions;
import services.discordhelpers.EmbedHelper;
import services.PermissionManager;
import services.database.DBHandlerPlace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import static services.discordhelpers.ReactionHelper.addReaction;

public class PlaceQueue implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceQueue.class);
    private static final Pattern argumentPattern = Pattern.compile("^(?:10000|[1-9][0-9]{0,3}|0)?$");
    private static final List<String> types = List.of("jpg", "jpeg", "png");

    @Override
    public void handle(CommandContext ctx) {
        ArrayList<Integer> ids = DBHandlerPlace.getPlaceProjectIDs();
        Random random = new Random();
        ArrayList<Pixel> pixels = new ArrayList<>();
        Scanner scanner;
        int id = -1;

        if (!ctx.getArguments().isEmpty() && PermissionManager.authenticateOwner(ctx)) {
            try {
                id = Integer.parseInt(ctx.getArguments().get(0));
            } catch (Exception e) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }
        } else {
            do {
                id = random.nextInt(10000);
            } while (ids.contains(id));
        }

        try {
            scanner = new Scanner(ctx.getMessage().getAttachments().get(0).getProxy().download().get());
        } catch (Exception e) {
            try {
                scanner = new Scanner(ctx.getMessage().getReferencedMessage().getAttachments().get(0)
                        .getProxy().download().get());
            } catch (Exception ee) {
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        long time = System.currentTimeMillis();

        while (scanner.hasNextLine()) {
            int x, y;
            double alpha;
            String color;
            try {
                String[] line = scanner.nextLine().replace(".place setpixel ", "").split(" ");
                x = Integer.parseInt(line[0]);
                y = Integer.parseInt(line[1]);
                color = line[2];
                alpha = (line.length == 4) ? Integer.parseInt(line[3]) / 255.0 : 1.0;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }
            pixels.add(new Pixel(x, y, alpha, color));
        }
        scanner.close();

        DBHandlerPlace.insertProjectIntoQueue(id, ctx.getAuthor().getId(), pixels);

        EmbedBuilder embed = EmbedHelper.embedBuilder("Queue");
        addReaction(ctx, 0);
        embed.setDescription("Your file got ID " + id);

        EmbedHelper.sendEmbed(ctx, embed, 32);
    }

    @Override
    public String getName() {
        return "PlaceQueue";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Queues a project to be drawn later");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("placeq", "pq");
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }

    @Override
    public boolean attachmentCheck(CommandContext ctx) {
        if (ctx.getMessage().getAttachments().isEmpty()) {
            return false;
        }

        String type = ctx.getMessage().getAttachments().get(0).getContentType().split("/")[1];

        return types.contains(type);
    }
}
