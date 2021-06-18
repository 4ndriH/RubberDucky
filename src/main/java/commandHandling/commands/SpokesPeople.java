package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import services.database.dbHandlerSpokesPeople;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpokesPeople implements CommandInterface {
    public SpokesPeople(Logger LOGGER) {
        LOGGER.info("Loaded Command SpokesPeople");
    }

    @Override
    public void handle(CommandContext ctx) {
        services.Logger.command(ctx, "spokespeople", true);
        EmbedBuilder embed = new EmbedBuilder();
        String firstYear = dbHandlerSpokesPeople.getPeople(1);
        String secondYear = dbHandlerSpokesPeople.getPeople(2);

        embed.setTitle("Semester Spokes People");
        embed.setColor(new Color(0xb074ad));
        embed.setThumbnail("attachment://vis.png");
        embed.addField("__First Year__", firstYear, true);
        embed.addBlankField(true);
        embed.addField("__Second Year__", secondYear, true);

        Message discordCacheRefresh = ctx.getChannel().sendMessage("beep boop").complete();
        discordCacheRefresh.editMessage(firstYear + "\n" + secondYear).complete();
        discordCacheRefresh.delete().queue();

        ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(new File("resources/vis.png"))
                .queue(msg -> msg.delete().queueAfter(32, TimeUnit.SECONDS));
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
