package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.regex.Pattern;

public class Status implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:(?:competing|listening|playing|watching)?\\s?.{1,128})?\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(Status.class);
    private static final Set<String> activities = Set.of("competing", "listening", "playing", "watching");

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();
        String activity = "playing";

        if (!ctx.getArguments().isEmpty()) {
            activity = ctx.getArguments().get(0).toLowerCase();

            if (activities.contains(activity)) {
                ctx.getArguments().remove(0);
            }

            for (String s : ctx.getArguments()) {
                sb.append(s).append(" ");
            }
        } else {
            sb.append("With Duckies");
        }

        switch (activity) {
            case "competing" -> ctx.getJDA().getPresence().setActivity(Activity.competing(sb.toString()));
            case "listening" -> ctx.getJDA().getPresence().setActivity(Activity.listening(sb.toString()));
            case "playing"   -> ctx.getJDA().getPresence().setActivity(Activity.playing(sb.toString()));
            default          -> ctx.getJDA().getPresence().setActivity(Activity.watching(sb.toString()));
        }

        LOGGER.debug("Status changed to: " + activity + " | " + sb);
    }

    @Override
    public String getName() {
        return "Status";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Changes the bots status");
        embed.addField("__Usage__", "```[competing|listening|playing|watching] <status>```\n" +
                "The status can be at most 128 chars.", false);
        return embed;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
