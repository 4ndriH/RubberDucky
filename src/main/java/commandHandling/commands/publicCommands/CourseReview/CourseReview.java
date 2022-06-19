package commandHandling.commands.publicCommands.CourseReview;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.BotExceptions;
import services.EmbedHelper;
import services.VVZScraper;
import services.database.DBHandlerCourse;
import services.database.DBHandlerCourseReview;
import services.database.DBHandlerCourseReviewVerify;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static services.MessageDeleteHelper.deleteMsg;
import static services.ReactionHelper.addReaction;

public class CourseReview implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseReview.class);
    private static HashMap<String, Integer> inputs = new HashMap<>();
    Pattern pattern = Pattern.compile("^\\w{3}-\\w{4}-\\w{2}(l|L)$");
    EmbedBuilder embed = EmbedHelper.embedBuilder("I hereby confirm that this is a constructive feedback and not just a rant");

    public CourseReview(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getArguments().size() == 0) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        } else if (!pattern.matcher(ctx.getArguments().get(0)).find()) {
            BotExceptions.invalidCourseNumber(ctx, "");
            return;
        }else if (inputs.containsKey(ctx.getAuthor().getId())) {
            EmbedHelper.sendEmbed(ctx, EmbedHelper.embedBuilder("You have an unfinished course feedback"), 64);
            return;
        }

        addReaction(ctx, 0);

        String feedback = ctx.getArguments().stream().skip(1).map(Object::toString).collect(Collectors.joining(" ")).replace("```", "");
        String courseNumber = ctx.getArguments().get(0).toUpperCase();

        DBHandlerCourseReview.insertCourseReview(ctx.getAuthor().getId(), feedback, courseNumber);

        inputs.put(ctx.getAuthor().getId(), DBHandlerCourse.getKeyOfReview(courseNumber, feedback));

        embed.setDescription(feedback);
        Message msg = ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                Button.danger("cfAbort - " + ctx.getAuthor().getId(), "Abort"),
                Button.success("cfProceed - " + ctx.getAuthor().getId(), "Proceed")
        ).complete();

        deleteMsg(msg, 256);

        if (!DBHandlerCourseReview.containsCourseNumber(courseNumber)) {
            DBHandlerCourseReview.insertCourse(courseNumber, VVZScraper.getCourseName(courseNumber));
        }
    }

    public static void processAbort(String discordUserId) {
        DBHandlerCourseReviewVerify.updateVerifiedStatus(inputs.get(discordUserId), -1);
        inputs.remove(discordUserId);
    }

    public static void processProceed(String discordUserId) {
        inputs.remove(discordUserId);
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
