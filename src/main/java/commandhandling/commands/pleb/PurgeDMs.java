package commandhandling.commands.pleb;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordhelpers.EmbedHelper;

import java.io.File;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class PurgeDMs implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PurgeDMs.class);
    private final EmbedBuilder purgeCommenced = EmbedHelper.embedBuilder("Happy purging");
    private final EmbedBuilder purgeEnded = EmbedHelper.embedBuilder("Thank you for participating in the purge <3");

    public PurgeDMs() {
        embedSetUp();
    }

    @Override
    public void handle(CommandContext ctx) {
        PrivateChannel channel = ctx.getAuthor().openPrivateChannel().complete();
        MessageCreateAction mca = channel.sendMessageEmbeds(purgeCommenced.build()).addFiles(FileUpload.fromData(new File("resources/images/purge/purgeCommenced.jpg")));
        sendMessage(mca, 32);

        channel.getIterableHistory().forEachAsync(msg -> {
            if (msg.getAuthor().isBot()) {
                msg.delete().queue();
                try {
                    Thread.sleep(1024);
                } catch (Exception ignored) {}
            }
            return true;
        }).exceptionally(e -> {
            LOGGER.error("Error purging dms", e);
            return null;
        }).whenComplete((ignored, ignored2) -> {
            MessageCreateAction mca2 = channel.sendMessageEmbeds(purgeEnded.build()).addFiles(FileUpload.fromData(new File("resources/images/purge/purgeEnded.jpg")));
            sendMessage(mca2, 32);
        });
    }

    private void embedSetUp() {
        purgeCommenced.setImage("attachment://purgeCommenced.jpg");
        purgeEnded.setImage("attachment://purgeEnded.jpg");
    }

    @Override
    public String getName() {
        return "PurgeDMs";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Deletes all DMs you and me have ;)");
        return embed;
    }
}
