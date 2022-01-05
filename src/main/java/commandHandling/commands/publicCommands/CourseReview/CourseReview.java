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
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CourseReview implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseReview.class);
    private static HashMap<String, ArrayList<String>> inputs = new HashMap<>();

    public CourseReview(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getArguments().size() == 0 || ctx.getArguments().get(0).split("-").length != 3 || !ctx.getArguments().get(0).endsWith("L")) {
            CommandManager.commandLogger(getName(), ctx, false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        } else if (inputs.containsKey(ctx.getAuthor().getId())) {
            EmbedHelper.sendEmbed(ctx, EmbedHelper.embedBuilder("You have an unfinished course feedback"), 64);
            return;
        }

        EmbedBuilder embed = EmbedHelper.embedBuilder("I hereby confirm that this is a constructive feedback and not just a rant");
        CommandManager.commandLogger(getName(), ctx, true);

        String feedback = ctx.getArguments().stream().skip(1).map(Object::toString).collect(Collectors.joining(" "));
        inputs.put(ctx.getAuthor().getId(), new ArrayList<>(ctx.getArguments()));
        String id = ctx.getAuthor().getId();

        embed.addField(ctx.getArguments().get(0), feedback, false);
        ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                Button.danger("cfAbort - " + id, "Abort"),
                Button.success("cfProceed - " + id, "Proceed")
        ).queue();

        if (DatabaseHandler.containsCourseNumber(ctx.getArguments().get(0)) <= 0) {
            DatabaseHandler.insertCourse(ctx.getArguments().get(0));
            LOGGER.warn("A new course has been added: " + ctx.getArguments().get(0) + "\n" +
                    "<http://www.vorlesungsverzeichnis.ethz.ch/Vorlesungsverzeichnis/sucheLehrangebot.view?lang=de&" +
                    "search=on&semkez=2022S&studiengangTyp=&deptId=&studiengangAbschnittId=&lerneinheitstitel=&" +
                    "lerneinheitscode=" + ctx.getArguments().get(0) + "&famname=&rufname=&wahlinfo=&" +
                    "lehrsprache=&periodizitaet=&katalogdaten=&_strukturAus=on&search=Suchen>\n\n" +
                    "rdsql update courses set courseName='' where courseNumber='" + ctx.getArguments().get(0) + "'");
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
