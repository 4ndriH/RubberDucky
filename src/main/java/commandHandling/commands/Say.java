package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import services.BotExceptions;
import services.DiscordLogger;

import java.util.HashMap;

public class Say implements CommandInterface {
    private volatile HashMap<String, Boolean> sayChannels = new HashMap<>();

    public Say(Logger LOGGER) {
        LOGGER.info("Loaded Command Say");
    }

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();
        String channel = ctx.getChannel().getId();
        int repeats;

        if (ctx.getArguments().size() == 0) {
            sayChannels.replace(channel, false);
            DiscordLogger.command(ctx, "say", true);
            return;
        } else if (ctx.getArguments().get(0).equalsIgnoreCase("stopAll")) {
            for (String key : sayChannels.keySet()) {
                sayChannels.replace(key, false);
            }
            DiscordLogger.command(ctx, "say", true);
            return;
        }

        try {
            repeats = Integer.parseInt(ctx.getArguments().get(0));
            for (int i = 1; i < ctx.getArguments().size(); i++) {
                sb.append(ctx.getArguments().get(i)).append(" ");
            }

            DiscordLogger.command(ctx, "say", true);
        } catch (Exception e) {
            DiscordLogger.command(ctx, "say", false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (!sayChannels.containsKey(channel)) {
            sayChannels.put(channel, true);
        }

        (new Thread(() -> {
            for (int i = 0; i < repeats && sayChannels.get(channel); i++) {
                ctx.getChannel().sendMessage(sb.toString()).complete();
            }
            sayChannels.remove(channel);
        })).start();
    }

    @Override
    public String getName() {
        return "Say";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Repeats a message a certain amount of times");
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
