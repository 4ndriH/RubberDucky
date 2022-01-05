package commandHandling.commands.publicCommands.CourseReview;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

public class Course implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(Course.class);

    public Course(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder();
        if (ctx.getArguments().size() == 0 || DatabaseHandler.containsCourseNumber(ctx.getArguments().get(0)) <= 0) {
            embed.setTitle("Looks like there are no reviews for course: "
                    + (ctx.getArguments().size() > 0 ? ctx.getArguments().get(0) : ""));
        } else {
            String title = DatabaseHandler.getCourse(ctx.getArguments().get(0));
            int titleLength = title.length();
            embed.setTitle(title);
            StringBuilder sb = new StringBuilder();
            for (String s : DatabaseHandler.getCourseReview(ctx.getArguments().get(0))) {
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
        embed.setDescription("Get reviews for a specific course");
        embed.addField("__Format__", "```" + CONFIG.Prefix.get() + "course <course number>```", false);
        return embed;
    }
}
