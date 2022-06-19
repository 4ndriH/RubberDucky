package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordHelpers.EmbedHelper;

import java.util.List;

public class Purge implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Purge.class);
    private final EmbedBuilder purgeCommenced = EmbedHelper.embedBuilder("Happy purging");
    private final EmbedBuilder busyPurging = EmbedHelper.embedBuilder("Already busy purging");
    private final EmbedBuilder purgeEnded = EmbedHelper.embedBuilder();
    private volatile boolean isRunning, stop;

    public Purge(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
        embedSetUp();
    }


    @Override
    public void handle(CommandContext ctx) {
        if (isRunning) {
            if (ctx.getArguments().size() == 1 && ctx.getArguments().get(0).equalsIgnoreCase("stop")) {
                stop = true;
            } else {
                EmbedHelper.sendEmbedWithFile(ctx, busyPurging, 32, "resources/purge/busyPurging.png", "busyPurging.png");
            }
            return;
        }

        isRunning = true;

        (new Thread(() -> {
            EmbedHelper.sendEmbedWithFile(ctx, purgeCommenced, 32, "resources/purge/purgeCommenced.jpg", "purgeCommenced.jpg");
            List<Message> messages;
            do {
                messages = ctx.getChannel().getHistory().retrievePast(64).complete();
                for (int i = 0; i < messages.size() && !stop; i++) {
                    messages.get(i).delete().complete();
                    try {
                        Thread.sleep(1024);
                    } catch (Exception ignored) {}
                }
            } while (messages.size() != 0 && !stop);
            EmbedHelper.sendEmbedWithFile(ctx, purgeEnded, 32, "resources/purge/purgeEnded.jpg", "purgeEnded.jpg");
            isRunning = stop = false;
        })).start();
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
