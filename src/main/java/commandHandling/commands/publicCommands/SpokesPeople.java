package commandHandling.commands.publicCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Miscellaneous;
import services.database.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpokesPeople implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(SpokesPeople.class);

    public SpokesPeople(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);
        ArrayList<HashMap<String, String>> spokesPeople = DatabaseHandler.getSpokesPeople();
        EmbedBuilder embed = Miscellaneous.embedBuilder("Semester Spokes People");
        StringBuilder yearOne = new StringBuilder();
        StringBuilder yearTwo = new StringBuilder();

        for (HashMap<String, String> sp : spokesPeople) {
            HashMap<String, String> temp = new HashMap<>();
            for (String key : sp.keySet()) {
                if (temp.containsKey(sp.get(key))) {
                    temp.replace(sp.get(key), temp.get(sp.get(key)) + "_" + key);
                } else {
                    temp.put(sp.get(key), key);
                }
            }
            if (yearOne.length() == 0) {
                for (String key : temp.keySet()) {
                    yearOne.append("**").append(key).append(":**\n");
                    String[] tempArr = temp.get(key).split("_");
                    for (String s : tempArr) {
                        yearOne.append("<@!").append(s).append(">\n");
                    }
                }
            } else {
                for (String key : temp.keySet()) {
                    yearTwo.append("**").append(key).append(":**\n");
                    String[] tempArr = temp.get(key).split("_");
                    for (String s : tempArr) {
                        yearTwo.append("<@!").append(s).append(">\n");
                    }
                }
            }
        }

        embed.setThumbnail("attachment://vis.png");
        embed.addField("__First Year__", yearOne.toString(), true);
        embed.addBlankField(true);
        embed.addField("__Second Year__", yearTwo.toString(), true);
        Message discordCacheRefresh = ctx.getChannel().sendMessage("beep boop").complete();
        discordCacheRefresh.editMessage(yearOne + "\n" + yearTwo).complete();
        discordCacheRefresh.delete().queue();
        ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(new File("resources/vis.png")).queue(
                        msg -> Miscellaneous.deleteMsg(msg, 64)
        );
    }

    @Override
    public String getName() {
        return "SpokesPeople";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns a list of all subjects and the corresponding spokes people");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ssp", "spokespeople");
    }
}
