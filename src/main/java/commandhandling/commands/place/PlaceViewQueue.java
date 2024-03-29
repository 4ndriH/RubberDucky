package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import services.miscellaneous.Format;
import services.database.DBHandlerPlace;
import services.discordhelpers.EmbedHelper;

import java.time.Instant;
import java.util.List;

import static services.database.DBHandlerPlace.getPixelsInQueue;
import static services.discordhelpers.MessageSendHelper.sendMessage;

public class PlaceViewQueue implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Queue");
        String[] strings = DBHandlerPlace.getPlaceProjectQueue();

        if (strings[0].isEmpty()) {
            embed.setDescription("The Queue is empty");
        } else {
            int pixelsInQueue = getPixelsInQueue();
            embed.setDescription("There are " + Format.Number(pixelsInQueue) + " pixels in the queue.\n" +
                    "Earliest completion <t:" + (Instant.now().getEpochSecond() + (int)(pixelsInQueue * 1.0587)) + ":R>");
            embed.addField("__ID__", strings[0], true);
            embed.addField("__Drawn Pixels__", strings[1], true);
        }

        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
        sendMessage(ctx, mca, 64);
    }

    @Override
    public String getName() {
        return "PlaceViewQueue";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns a list of all the current projects in the queue");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pvq");
    }
}
