package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordhelpers.EmbedHelper;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class Purge implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:stop)?\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(Purge.class);
    private final EmbedBuilder purgeCommenced = EmbedHelper.embedBuilder("Happy purging");
    private final EmbedBuilder busyPurging = EmbedHelper.embedBuilder("Already busy purging");
    private final EmbedBuilder purgeEnded = EmbedHelper.embedBuilder("Thank you for participating in the purge <3");
    private static final AtomicBoolean stop = new AtomicBoolean(false);
    private static final AtomicBoolean purging = new AtomicBoolean(false);

    public Purge() {
        embedSetUp();
    }

    @Override
    public void handle(CommandContext ctx) {
        if (!ctx.getArguments().isEmpty()) {
            stop.set(true);
            purging.set(false);
            return;
        }

        if (purging.get()) {
            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(busyPurging.build()).addFiles(FileUpload.fromData(new File("resources/images/purge/busyPurging.png")));
            sendMessage(mca, 32);
            return;
        } else {
            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(purgeCommenced.build()).addFiles(FileUpload.fromData(new File("resources/images/purge/purgeCommenced.jpg")));
            sendMessage(mca, 32);
            purging.set(true);
        }


        ctx.getChannel().getIterableHistory().forEachAsync(msg -> {
            msg.delete().queue();
            try {
                Thread.sleep(1024);
            } catch (Exception ignored) {}
            return !stop.get();
        }).exceptionally(e -> {
            LOGGER.error("Error purging", e);
            return null;
        }).whenComplete((ignored, ignored2) -> {
            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(purgeEnded.build()).addFiles(FileUpload.fromData(new File("resources/images/purge/purgeEnded.jpg")));
            sendMessage(mca, 32);
            stop.set(false);
            purging.set(false);
        });
    }

    private void embedSetUp() {
        purgeCommenced.setImage("attachment://purgeCommenced.jpg");
        busyPurging.setImage("attachment://busyPurging.png");
        purgeEnded.setImage("attachment://purgeEnded.jpg");
    }

    @Override
    public String getName() {
        return "Purge";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Deletes all messages in the current channel");
        return embed;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
