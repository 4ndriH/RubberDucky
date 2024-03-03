package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import assets.objects.PlaceData;

public class PlaceStop implements CommandInterface {
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
    public int getRestrictionLevel() {
        return 0;
    }
}
