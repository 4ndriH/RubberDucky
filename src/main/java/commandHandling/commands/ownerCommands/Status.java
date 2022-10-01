package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;

import static services.discordHelpers.ReactionHelper.addReaction;

public class Status implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Status.class);

    public Status(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();
        String activity;

        try {
            activity = ctx.getArguments().get(0);
            for (int i = 1; i < ctx.getArguments().size(); i++) {
                sb.append(ctx.getArguments().get(i)).append(" ");
            }
            if (sb.toString().length() > 128 || sb.toString().toCharArray().length == 0) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }
        } catch (Exception e) {
            activity = "";
        }

        addReaction(ctx, 0);

        switch (activity) {
            case "competing" -> ctx.getJDA().getPresence().setActivity(Activity.competing(sb.toString()));
            case "listening" -> ctx.getJDA().getPresence().setActivity(Activity.listening(sb.toString()));
            case "playing" -> ctx.getJDA().getPresence().setActivity(Activity.playing(sb.toString()));
            case "watching" -> ctx.getJDA().getPresence().setActivity(Activity.watching(sb.toString()));
            default -> ctx.getJDA().getPresence().setActivity(Activity.playing("With Duckies"));
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
    public int getRestrictionLevel() {
        return 0;
    }
}
