package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import resources.CONFIG;
import services.BotExceptions;
import services.DiscordLogger;

import java.util.HashMap;

public class Say implements CommandInterface {
    private volatile HashMap<String, Boolean> runningChannels = new HashMap<>();

    public Say(Logger LOGGER) {
        LOGGER.info("Loaded Command Say");
    }

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();
        String channel = ctx.getChannel().getId();
        int repeats;

        if (runningChannels.containsKey(ctx.getChannel().getId())) {
            DiscordLogger.command(ctx, "say", true);
            runningChannels.put(channel, false);
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

        (new Thread(() -> {
            runningChannels.put(channel, true);
            for (int i = 0; i < repeats && runningChannels.get(channel); i++) {
                ctx.getChannel().sendMessage(sb.toString()).complete();
            }
            runningChannels.remove(channel);
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
        embed.addField("__How To:__", "```" + CONFIG.Prefix.get() +
                "say <amount> <message>```", false);
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
