package commandHandling.commands.placeCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Objects.PlaceData;

import java.util.List;

public class PlaceStop implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceStop.class);

    public PlaceStop(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        PlaceData.stop = true;
    }

    @Override
    public String getName() {
        return "PlaceStop";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Stops the drawing process");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ps");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
