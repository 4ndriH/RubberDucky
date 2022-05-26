package commandHandling.commands.publicCommands.CourseReview;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerCourseReviewVerify;
import services.logging.EmbedHelper;

import java.util.List;
import java.util.Map;

public class CourseReviewVerify implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseReviewVerify.class);
    private static Map<Integer, String[]> reviews;
    private static CommandContext ctx;

    public CourseReviewVerify(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        reviews = DBHandlerCourseReviewVerify.getUnverifiedReviews();
        CourseReviewVerify.ctx = ctx;
        sendEmbed();
    }

    public static void castVerdict(int key, int status) {
        if (status != 0) {
            DBHandlerCourseReviewVerify.updateVerifiedStatus(key, status);
            LOGGER.info("Review " + key + " has been " + (status == 1 ? "accepted" : "rejected"));
            reviews.remove(key);
            sendEmbed();
        }
    }

    private static void sendEmbed() {
        if (!reviews.isEmpty()) {
            Map.Entry<Integer, String[]> entry = reviews.entrySet().iterator().next();
            EmbedBuilder embed = EmbedHelper.embedBuilder("Course Feedback: " + entry.getKey());
            embed.setDescription(entry.getValue()[0]);
            embed.setFooter(entry.getValue()[1]);
            ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                    Button.danger("$cfvReject - " + entry.getKey(), "Reject"),
                    Button.primary("$cfvQuit - " + entry.getKey(), "Quit"),
                    Button.success("$cfvAccept - " + entry.getKey(), "Accept")
            ).queue();
        } else {
            EmbedHelper.sendEmbed(ctx, EmbedHelper.embedBuilder("Nothing to review"), 32);
        }
    }

    @Override
    public String getName() {
        return "CourseReviewVerify";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Weed out course feedbacks which are pointless rants");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("crv");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
