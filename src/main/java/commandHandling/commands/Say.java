package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import services.BotExceptions;

import java.awt.*;

public class Say implements CommandInterface {
    private volatile boolean isRunning, stop;

    public Say(Logger LOGGER) {
        LOGGER.info("Loaded Command Say");
    }

    @Override
    public void handle(CommandContext ctx) {
        int repeats;
        StringBuilder sb = new StringBuilder();

        if (isRunning) {
            services.Logger.command(ctx, "say", true);
            stop = true;
            return;
        }

        try {
            repeats = Integer.parseInt(ctx.getArguments().get(0));
            for (int i = 1; i < ctx.getArguments().size(); i++) {
                sb.append(ctx.getArguments().get(i)).append(" ");
            }
            services.Logger.command(ctx, "say", true);
        } catch (Exception e) {
            services.Logger.command(ctx, "say", false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        (new Thread(() -> {
            isRunning = true;
            for (int i = 0; i < repeats && !stop; i++) {
                ctx.getChannel().sendMessage(sb.toString()).complete();
            }
            isRunning = stop = false;
        })).start();
    }

    @Override
    public String getName() {
        return "Say";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Say");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Repeats a message a defined amount of times");
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
