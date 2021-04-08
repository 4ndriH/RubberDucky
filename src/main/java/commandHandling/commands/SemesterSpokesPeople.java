package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import services.database.dbHandlerSpokesPeople;

import java.awt.*;

public class SemesterSpokesPeople implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder();
        String firstYear = dbHandlerSpokesPeople.getPeople(1);
        String secondYear = dbHandlerSpokesPeople.getPeople(2);

        embed.setTitle("Semester Spokes People");
        embed.setColor(new Color(0xb074ad));
        embed.addField("__First Year__", firstYear, true);
        embed.addBlankField(true);
        embed.addField("__Second Year__", secondYear, true);

        ctx.getChannel().sendMessage(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "ssp";
    }

    @Override
    public String getHelp() {
        return null;
    }
}
