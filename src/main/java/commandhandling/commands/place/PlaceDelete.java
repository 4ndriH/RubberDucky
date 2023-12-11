package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Config;
import services.BotExceptions;
import services.database.DBHandlerPlace;
import services.discordhelpers.EmbedHelper;

import java.io.File;
import java.util.ArrayList;

public class PlaceDelete implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceDelete.class);

    public PlaceDelete(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        ArrayList<Integer> ids = DBHandlerPlace.getPlaceProjectIDs();
        EmbedBuilder embed = EmbedHelper.embedBuilder("Delete");
        int id;

        try {
            id = Integer.parseInt(ctx.getArguments().get(0));
            if (ids.contains(id)) {
                File myTxtObj = new File("tempFiles/place/queue/RDdraw" + id + ".txt");
                DBHandlerPlace.removeProjectFromQueue(id);
                while(myTxtObj.exists() && !myTxtObj.delete());
                embed.setDescription("File " + id + " has been deleted");
            } else {
                embed.setDescription("There is no file with id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        EmbedHelper.sendEmbed(ctx, embed, 32);
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
}
