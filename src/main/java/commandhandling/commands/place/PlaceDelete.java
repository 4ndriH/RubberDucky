package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Config;
import services.database.DBHandlerPlace;
import services.discordhelpers.EmbedHelper;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class PlaceDelete implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:10000|[1-9][0-9]{0,3}|0)\\s?$");
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceDelete.class);

    @Override
    public void handle(CommandContext ctx) {
        ArrayList<Integer> ids = DBHandlerPlace.getPlaceProjectIDs();
        EmbedBuilder embed = EmbedHelper.embedBuilder("Delete");
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(0));
            if (ids.contains(id)) {
                DBHandlerPlace.removeProjectFromQueue(id);
                embed.setDescription("Project " + id + " has been deleted");
            } else {
                embed.setDescription("There is no project with id: " + id);
            }
        } catch (Exception e) {
            LOGGER.error("Something slipped past the argument check in PlaceDelete", e);
            return;
        }

        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
        sendMessage(mca, 32);
    }

    @Override
    public String getName() {
        return "PlaceDelete";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Deletes the project with the given ID");
        embed.addField("__Usage__", "```" + Config.prefix + getName() + " <ID>```", false);
        return embed;
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
