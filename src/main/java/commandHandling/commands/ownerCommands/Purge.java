package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Miscellaneous;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Purge implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Purge.class);
    private final EmbedBuilder purgeCommenced = Miscellaneous.embedBuilder("Happy purging");
    private final EmbedBuilder busyPurging = Miscellaneous.embedBuilder("Already busy purging");
    private final EmbedBuilder purgeEnded = Miscellaneous.embedBuilder();
    private volatile boolean isRunning, stop;

    public Purge(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
        embedSetUp();
    }


    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);

        if (isRunning) {
            if (ctx.getArguments().size() == 1 && ctx.getArguments().get(0).equalsIgnoreCase("stop")) {
                stop = true;
            } else {
                ctx.getChannel().sendMessageEmbeds(busyPurging.build())
                        .addFile(new File("resources/purge/busyPurging.png")).queue(
                                msg -> Miscellaneous.deleteMsg(msg, 32)
                );
            }
            return;
        }

        isRunning = true;

        (new Thread(() -> {
            ctx.getChannel().sendMessageEmbeds(purgeCommenced.build())
                    .addFile(new File("resources/purge/purgeCommenced.jpg")).queueAfter(1, TimeUnit.SECONDS);
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
            ctx.getChannel().sendMessageEmbeds(purgeEnded.build())
                    .addFile(new File("resources/purge/purgeEnded.jpg")).queue(
                    msg -> Miscellaneous.deleteMsg(msg, 32)
            );
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
    public boolean isOwnerOnly() {
        return true;
    }
}
