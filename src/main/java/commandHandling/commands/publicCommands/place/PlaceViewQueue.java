package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

import java.util.List;

public class PlaceViewQueue implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceViewQueue.class);

    public PlaceViewQueue(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Queue");
        String[] strings = DatabaseHandler.getPlaceQueue();

        if (strings[0].length() == 0) {
            embed.setDescription("The Queue is empty");
        } else {
            embed.addField("__ID__", strings[0], true);
            embed.addField("__Drawn Pixels__", strings[1], true);
            embed.addField("__Queued by__", strings[2], true);
        }

        EmbedHelper.sendEmbed(ctx, embed, 64);
    }

    @Override
    public String getName() {
        return "PlaceViewQueue";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pvq");
    }
}
