package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import assets.objects.Pixel;
import services.BotExceptions;
import services.database.daos.PlaceProjectsDAO;
import services.discordhelpers.EmbedHelper;
import services.PermissionManager;

import java.util.*;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class PlaceQueue implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:10000|[1-9][0-9]{0,3}|0)?\\s?$");

    @Override
    public void handle(CommandContext ctx) {
        PlaceProjectsDAO placeProjectsDAO = new PlaceProjectsDAO();
        List<Integer> ids = placeProjectsDAO.getProjectIds();
        Random random = new Random();
        ArrayList<Pixel> pixels = new ArrayList<>();
        Scanner scanner;
        int id;

        if (!ctx.getArguments().isEmpty() && PermissionManager.authenticateOwner(ctx)) {
            try {
                id = Integer.parseInt(ctx.getArguments().get(0));
            } catch (Exception e) {
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
                scanner = new Scanner(Objects.requireNonNull(ctx.getMessage().getReferencedMessage()).getAttachments().get(0).getProxy().download().get());
            } catch (Exception ee) {
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

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

        placeProjectsDAO.queueProject(ctx.getAuthor().getId(), id);

        EmbedBuilder embed = EmbedHelper.embedBuilder("Queue");
        embed.setDescription("Your file got ID " + id);

        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
        sendMessage(ctx, mca, 32);
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
            Message refMsg = ctx.getMessage().getReferencedMessage();

            return refMsg != null && !refMsg.getAttachments().isEmpty();
        }

        String type = Objects.requireNonNull(ctx.getMessage().getAttachments().get(0).getContentType()).split("/")[1];

        return type.contains("plain");
    }
}
