package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordhelpers.EmbedHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Purge implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Purge.class);
    private final EmbedBuilder purgeCommenced = EmbedHelper.embedBuilder("Happy purging");
    private final EmbedBuilder busyPurging = EmbedHelper.embedBuilder("Already busy purging");
    private final EmbedBuilder purgeEnded = EmbedHelper.embedBuilder();
    private static final AtomicBoolean isRunning = new AtomicBoolean(false);
    private static final AtomicBoolean stop = new AtomicBoolean(false);

    public Purge() {
        embedSetUp();
    }


    @Override
    public void handle(CommandContext ctx) {
        if (isRunning.compareAndSet(false, true)) {
            (new Thread(() -> {
                List<Message> messages = ctx.getChannel().getHistory().retrievePast(64).complete();
                EmbedHelper.sendEmbedWithFile(ctx, purgeCommenced, 32, "resources/purge/purgeCommenced.jpg", "purgeCommenced.jpg");
                do {
                    for (int i = 0; i < messages.size() && !stop.get(); i++) {
                        messages.get(i).delete().queue();
                        try {
                            Thread.sleep(2048);
                        } catch (Exception ignored) {}
                    }
                    messages = ctx.getChannel().getHistory().retrievePast(64).complete();
                } while (messages.size() != 0 && !stop.get());
                EmbedHelper.sendEmbedWithFile(ctx, purgeEnded, 32, "resources/purge/purgeEnded.jpg", "purgeEnded.jpg");
                isRunning.set(false);
                stop.set(false);
            })).start();
        } else {
            if (ctx.getArguments().size() == 1 && ctx.getArguments().get(0).equalsIgnoreCase("stop")) {
                stop.set(true);
            } else {
                EmbedHelper.sendEmbedWithFile(ctx, busyPurging, 32, "resources/purge/busyPurging.png", "busyPurging.png");
            }
        }
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
    public int getRestrictionLevel() {
        return 0;
    }
}
