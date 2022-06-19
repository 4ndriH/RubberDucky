package commandHandling.commands.publicCommands.CourseReview;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.EmbedHelper;
import services.Objects.Review;
import services.database.DBHandlerCourseReviewVerify;

import java.util.List;
import java.util.Map;

import static services.database.DBHandlerCourse.getCourseName;

public class CourseReviewVerify implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseReviewVerify.class);
    private static Map<Integer, Review> reviews;
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
            Review review  = reviews.entrySet().iterator().next().getValue();
            EmbedBuilder embed = EmbedHelper.embedBuilder(review.courseNumber + " - " + getCourseName(review.courseNumber));
            embed.setDescription(review.review);

            if (review.discordUserId != null) {
                embed.setFooter(ctx.getJDA().getUserById(review.discordUserId).getAsTag());
            } else {
                embed.setFooter("eth_id");
            }

            ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                    Button.danger("cfvReject - " + review.key + " - " + ctx.getAuthor().getId(), "Reject"),
                    Button.primary("cfvQuit - " + review.key + " - " + ctx.getAuthor().getId(), "Quit"),
                    Button.success("cfvAccept - " + review.key + " - " + ctx.getAuthor().getId(), "Accept")
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
        return 1;
    }
}
