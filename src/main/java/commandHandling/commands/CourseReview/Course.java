package commandHandling.commands.CourseReview;

import assets.CONFIG;
import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.database.DBHandlerCourse;
import services.discordHelpers.EmbedHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static services.database.DBHandlerCourse.getCourseName;
import static services.database.DBHandlerCourse.getReviewsForCourse;
import static services.discordHelpers.ReactionHelper.addReaction;

public class Course implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(Course.class);

    public Course(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        HashSet<String> courses = DBHandlerCourse.getCoursesWithVerifiedReviews();
        EmbedBuilder embed = EmbedHelper.embedBuilder();

        if (ctx.getArguments().size() == 0) {
            embed.setTitle("Course reviews", "https://n.ethz.ch/~lteufelbe/coursereview/");
            StringBuilder sb = new StringBuilder();

            for (String course : courses) {
                sb.append(getCourseName(course)).append("\n");
            }

            embed.setDescription(sb);
        } else {
            String courseNumber = ctx.getArguments().get(0);
            String course = getCourseName(ctx.getArguments().get(0));

            if (course.length() == 0) {
                BotExceptions.invalidCourseNumber(ctx, "\nUse `" + CONFIG.prefix + "course` to get a list of courses with reviews");
                return;
            }

            embed.setTitle(course, "https://n.ethz.ch/~lteufelbe/coursereview/course/" + courseNumber + "/");
            ArrayList<String> reviews = getReviewsForCourse(ctx.getArguments().get(0));

            if (reviews.isEmpty()) {
                embed.setDescription("There are no reviews");
            } else {
                StringBuilder sb = new StringBuilder();

                for (String s : reviews) {
                    if (sb.length() + s.length() < 4096) {
                        sb.append("```").append(s).append("```");
                    } else {
                        LOGGER.warn("There is a course with reviews exceeding the char limit. You better have the pages implemented!");
                        break;
                    }
                }

                addReaction(ctx, 0);
                embed.setDescription(sb.toString());
            }
        }

        EmbedHelper.sendEmbed(ctx, embed, 512);
    }

    @Override
    public String getName() {
        return "Course";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Get reviews for a specific course. If the course has no reviews or no course number is provided you get a list with all available courses.");
        embed.addField("__Format__", "```" + CONFIG.prefix + "course <course number>```", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("c");
    }
}
