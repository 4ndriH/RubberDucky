package commandHandling.commands.publicCommands.CourseReview;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.BotExceptions;
import services.CommandManager;
import services.VVZScraper;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CourseReview implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseReview.class);
    private static HashMap<String, ArrayList<String>> inputs = new HashMap<>();
    Pattern pattern = Pattern.compile("^\\w{3}-\\w{4}-\\w{2}(l|L)$");

    public CourseReview(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getArguments().size() == 0) {
            CommandManager.commandLogger(getName(), ctx, false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        } else if (!pattern.matcher(ctx.getArguments().get(0)).find()) {
            CommandManager.commandLogger(getName(), ctx, false);
            BotExceptions.invalidCourseNumber(ctx, "");
            return;
        }else if (inputs.containsKey(ctx.getAuthor().getId())) {
            CommandManager.commandLogger(getName(), ctx, false);
            EmbedHelper.sendEmbed(ctx, EmbedHelper.embedBuilder("You have an unfinished course feedback"), 64);
            return;
        }

        EmbedBuilder embed = EmbedHelper.embedBuilder("I hereby confirm that this is a constructive feedback and not just a rant");
        CommandManager.commandLogger(getName(), ctx, true);

        String feedback = ctx.getArguments().stream().skip(1).map(Object::toString).collect(Collectors.joining(" "));
        inputs.put(ctx.getAuthor().getId(), new ArrayList<>(ctx.getArguments()));
        embed.setDescription(feedback);

        ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                Button.danger("cfAbort - " + ctx.getAuthor().getId(), "Abort"),
                Button.success("cfProceed - " + ctx.getAuthor().getId(), "Proceed")
        ).queue();

        if (!DatabaseHandler.containsCourseNumber(ctx.getArguments().get(0))) {
            String courseNumber = ctx.getArguments().get(0);
            DatabaseHandler.insertCourse(courseNumber, VVZScraper.getCourseName(courseNumber));
        }
    }

    public static void processAbort(String id) {
        inputs.remove(id);
    }

    public static void processProceed(String userId) {
        String feedback = inputs.get(userId).stream().skip(1).map(Object::toString).collect(Collectors.joining(" "));
        DatabaseHandler.insertCourseReview(userId, feedback, inputs.get(userId).get(0));
        inputs.remove(userId);
    }

    @Override
    public String getName() {
        return "CourseReview";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Add a review to a course you have visited to help other students decide what they want to do.");
        embed.addField("__Format__", "```" + CONFIG.Prefix.get() + "coursereview <course number> <review>```", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("cr");
    }

    @Override
    public boolean requiresFurtherChecks() {
        return true;
    }
}
