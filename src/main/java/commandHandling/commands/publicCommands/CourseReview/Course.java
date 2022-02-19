package commandHandling.commands.publicCommands.CourseReview;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.BotExceptions;
import services.CommandManager;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Course implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(Course.class);
    Pattern pattern = Pattern.compile("^\\w{3}-\\w{4}-\\w{2}(l|L)$");

    public Course(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        HashSet<String> courses = DatabaseHandler.getAllCourses();
        EmbedBuilder embed = EmbedHelper.embedBuilder();

        if (ctx.getArguments().size() == 0) {
            embed.setTitle("Courses with reviews");
            StringBuilder sb = new StringBuilder();
            for (String course : courses) {
                sb.append(DatabaseHandler.getCourse(course)).append("\n");
            }
            embed.setDescription(sb);
        } else if (!pattern.matcher(ctx.getArguments().get(0)).find()) {
            CommandManager.commandLogger(getName(), ctx, false);
            BotExceptions.invalidCourseNumber(ctx, "\nUse `" + CONFIG.Prefix.get() + "course` to get a list of courses with reviews");
            return;
        } else if (!courses.contains(ctx.getArguments().get(0))) {
            embed.setTitle("There are no reviews for " + ctx.getArguments().get(0));
            if (DatabaseHandler.containsCourseNumber(ctx.getArguments().get(0))) {
                embed.setDescription(DatabaseHandler.getCourse(ctx.getArguments().get(0)));
            }
        } else {
            ArrayList<String> reviews = DatabaseHandler.getCourseReview(ctx.getArguments().get(0));
            embed.setTitle(DatabaseHandler.getCourse(ctx.getArguments().get(0)));
            StringBuilder sb = new StringBuilder();

            for (String s : reviews) {
                if (sb.length() + s.length() < 4096) {
                    sb.append("```").append(s).append("```");
                } else {
                    LOGGER.warn("There is a course with reviews exceeding the char limit. You better have the pages implemented!");
                    break;
                }
            }

            CommandManager.commandLogger(getName(), ctx, true);
            embed.setDescription(sb.toString());
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
