package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import assets.objects.PlaceData;

import java.util.List;

public class PlaceStopQueue implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        PlaceData.stopQ = !PlaceData.stopQ;
    }

    @Override
    public String getName() {
        return "PlaceStopQueue";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Toggles whether or not the queue is stopped after the current project is finished.");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("psq");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
