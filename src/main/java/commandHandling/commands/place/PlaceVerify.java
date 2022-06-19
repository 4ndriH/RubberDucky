package commandHandling.commands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.place.PlaceData;

import java.util.List;

public class PlaceVerify implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceVerify.class);

    public PlaceVerify(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        PlaceData.verify = !PlaceData.verify;
    }

    @Override
    public String getName() {
        return "PlaceVerify";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Toggles whether or not placed pixels are getting verified");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pv");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
