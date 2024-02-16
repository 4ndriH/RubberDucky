package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordhelpers.EmbedHelper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class Purge implements CommandInterface {
    public static final Pattern argumentPattern = Pattern.compile("^(?:stop)?\\s?$");
    private final Logger LOGGER = LoggerFactory.getLogger(Purge.class);
    private final EmbedBuilder purgeCommenced = EmbedHelper.embedBuilder("Happy purging");
    private final EmbedBuilder busyPurging = EmbedHelper.embedBuilder("Already busy purging");
    private final EmbedBuilder purgeEnded = EmbedHelper.embedBuilder();
    private static final AtomicBoolean stop = new AtomicBoolean(false);

    public Purge() {
        embedSetUp();
    }

    @Override
    public void handle(CommandContext ctx) {
        if (!ctx.getArguments().isEmpty()) {
            stop.set(true);
            return;
        }

        EmbedHelper.sendEmbedWithFile(ctx, purgeCommenced, 32, "resources/images/purge/purgeCommenced.jpg", "purgeCommenced.jpg");
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
            EmbedHelper.sendEmbedWithFile(ctx, purgeEnded, 32, "resources/images/purge/purgeEnded.jpg", "purgeEnded.jpg");
        });
    }

    private void embedSetUp() {
        purgeCommenced.setImage("attachment://purgeCommenced.jpg");
        busyPurging.setImage("attachment://busyPurging.png");
        purgeEnded.setTitle("Thank you for participating in the purge <3");
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
