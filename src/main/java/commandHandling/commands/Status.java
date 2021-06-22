package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import services.BotExceptions;
import services.DiscordLogger;

public class Status implements CommandInterface {
    public Status(Logger LOGGER) {
        LOGGER.info("Loaded Command Status");
    }

    @Override
    public void handle(CommandContext ctx) {
        String activity;
        StringBuilder sb = new StringBuilder();

        try {
            activity = ctx.getArguments().get(0);
            for (int i = 1; i < ctx.getArguments().size(); i++) {
                sb.append(ctx.getArguments().get(i)).append(" ");
            }
            if (sb.toString().length() > 128 || sb.toString().toCharArray().length == 0) {
                DiscordLogger.command(ctx, "status", false);
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }
        } catch (Exception e) {
            activity = "";
        }

        DiscordLogger.command(ctx, "Status", true);

        switch (activity) {
            case "competing":
                ctx.getJDA().getPresence().setActivity(Activity.competing(sb.toString()));
                break;
            case "listening":
                ctx.getJDA().getPresence().setActivity(Activity.listening(sb.toString()));
                break;
            case "playing":
                ctx.getJDA().getPresence().setActivity(Activity.playing(sb.toString()));
                break;
            case "watching":
                ctx.getJDA().getPresence().setActivity(Activity.watching(sb.toString()));
                break;
            default:
                ctx.getJDA().getPresence().setActivity(Activity.playing("With Duckies"));
        }
    }

    @Override
    public String getName() {
        return "Status";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Changes the bots status");
        embed.addField("__Activities__", "competing\nlistening\nplaying\nwatching", false);
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
