package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import assets.Config;
import assets.objects.Pixel;
import services.BotExceptions;
import services.database.daos.PlacePixelsDAO;
import services.database.daos.PlaceProjectsDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class PlaceGetFile implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:10000|[1-9][0-9]{0,3}|0)\\s?$");

    @Override
    public void handle(CommandContext ctx) {
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(0));
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        PlaceProjectsDAO placeProjectsDAO = new PlaceProjectsDAO();

        if (placeProjectsDAO.getProjectIds().contains(id)) {
            try {
                PlacePixelsDAO placePixelsDAO = new PlacePixelsDAO();
                ArrayList<Pixel> pixels = placePixelsDAO.getPixels(id);
                String output = pixels.stream().map(Objects::toString).collect(Collectors.joining("\n"));

                MessageCreateAction mca = ctx.getChannel().sendFiles(FileUpload.fromData(output.getBytes(), "RDdraw" + id + ".txt"));
                sendMessage(ctx, mca, 128);
            } catch (IllegalArgumentException e) {
                BotExceptions.FileExceedsUploadLimitException(ctx);
            }
        } else {
            BotExceptions.fileDoesNotExistException(ctx);
        }
    }

    @Override
    public String getName() {
        return "PlaceGetFile";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns the project with the given ID");
        embed.addField("__Usage__", "```" + Config.PREFIX + getName() + " <ID>```", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pgf");
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
