package commandHandling.commands.publicCommands.CourseReview;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Course implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(Course.class);

    public Course(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        ArrayList<String> reviews;
        EmbedBuilder embed = EmbedHelper.embedBuilder();
        if (ctx.getArguments().size() == 0 || DatabaseHandler.containsCourseNumber(ctx.getArguments().get(0)) <= 0 || (reviews = DatabaseHandler.getCourseReview(ctx.getArguments().get(0))).size() == 0) {
            ArrayList<String> courses = DatabaseHandler.getAllCourses();

            embed.setTitle("Looks like there are no (verified) reviews for course: " + (ctx.getArguments().size() > 0 ? ctx.getArguments().get(0) : "-"));
            embed.addField("Courses with reviews", courses.stream().skip(1).map(Object::toString).collect(Collectors.joining("\n")), false);
        } else {
            String title = DatabaseHandler.getCourse(ctx.getArguments().get(0));
            StringBuilder sb = new StringBuilder();
            int titleLength = title.length();
            embed.setTitle(title);

            for (String s : reviews) {
                if (sb.length() + titleLength + s.length() < 6000) {
                    sb.append("```").append(s).append("```");
                } else {
                    LOGGER.warn("There is a course with reviews exceeding the char limit. You better have the pages implemented!");
                    break;
                }
            }
            embed.addField("Reviews", sb.toString(), false);
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
        embed.addField("__Format__", "```" + CONFIG.Prefix.get() + "course <course number>```", false);
        return embed;
    }
}
