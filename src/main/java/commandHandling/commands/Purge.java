package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Purge implements CommandInterface {
    private volatile boolean isRunning;
    private volatile boolean stop;
    private final EmbedBuilder purgeCommenced = new EmbedBuilder();
    private final EmbedBuilder busyPurging = new EmbedBuilder();

    public Purge(Logger LOGGER) {
        LOGGER.info("Loaded Command Purge");
        embedSetUp();
    }

    @Override
    public void handle(CommandContext ctx) {
        if (!services.PermissionManager.authenticateOwner(ctx)) {
            services.Logger.command(ctx, "delete", false);
            return;
        }

        if (isRunning) {
            if (ctx.getArguments().size() == 1 && ctx.getArguments().get(0).equalsIgnoreCase("stop")) {
                stop = true;
            } else {
                ctx.getChannel().sendMessage(busyPurging.build())
                        .addFile(new File("resources/busyPurging.png")).queue(
                                message -> message.delete().queueAfter(32, TimeUnit.SECONDS)
                );
            }
            services.Logger.command(ctx, "purge", true);
            return;
        } else {
            isRunning = true;
        }

        services.Logger.command(ctx, "purge", true);

        (new Thread(() -> {
            ctx.getChannel().sendMessage(purgeCommenced.build())
                    .addFile(new File("resources/purgeCommenced.jpg")).queueAfter(1, TimeUnit.SECONDS);
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
            isRunning = stop = false;
        })).start();
    }

    private void embedSetUp() {
        purgeCommenced.setColor(new Color(0xb074ad));
        purgeCommenced.setImage("attachment://purgeCommenced.jpg");
        busyPurging.setTitle("Already busy purging");
        busyPurging.setColor(new Color(0xb074ad));
        busyPurging.setImage("attachment://busyPurging.png");
    }

    @Override
    public String getName() {
        return "Purge";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Purge");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Deletes all messages in the current channel");
        return embed;
    }
}