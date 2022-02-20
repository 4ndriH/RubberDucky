package services;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import commandHandling.commands.adminCommands.LockDown;
import commandHandling.commands.adminCommands.Nuke;
import commandHandling.commands.modCommands.BlackList;
import commandHandling.commands.modCommands.Channel;
import commandHandling.commands.ownerCommands.*;
import commandHandling.commands.publicCommands.*;
import commandHandling.commands.publicCommands.CourseReview.Course;
import commandHandling.commands.publicCommands.CourseReview.CourseReview;
import commandHandling.commands.publicCommands.CourseReview.CourseReviewVerify;
import commandHandling.commands.publicCommands.place.PlaceDelete;
import commandHandling.commands.publicCommands.place.PlaceGetFile;
import commandHandling.commands.publicCommands.place.PlaceQueue;
import commandHandling.commands.publicCommands.place.PlaceViewQueue;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import resources.EMOTES;
import services.logging.EmbedHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private static final Logger cmdLogger = LoggerFactory.getLogger("Command Logger");
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new About(LOGGER));
        addCommand(new Avatar(LOGGER));
        addCommand(new BlackList(LOGGER));
        addCommand(new Channel(this, LOGGER));
        addCommand(new Course(LOGGER));
        addCommand(new CourseReview(LOGGER));
        addCommand(new CourseReviewVerify(LOGGER));
        addCommand(new Delete(LOGGER));
        addCommand(new Ducky(LOGGER));
        addCommand(new ExportDatabase(LOGGER));
        addCommand(new Help(this, LOGGER));
        addCommand(new Kill(LOGGER));
        addCommand(new LetMeGoogleThatForYou(LOGGER));
        addCommand(new LockDown(LOGGER));
        addCommand(new NickName(LOGGER));
        addCommand(new Nuke(LOGGER));
        addCommand(new Ping(LOGGER));
        addCommand(new Prefix(LOGGER));
        addCommand(new ProfilePicture(LOGGER));
        addCommand(new Purge(LOGGER));
        addCommand(new PurgeDMs(LOGGER));
        addCommand(new Say(LOGGER));
        addCommand(new Servers(LOGGER));
        addCommand(new SpokesPeople(LOGGER));
        addCommand(new SQL(LOGGER));
        addCommand(new Status(LOGGER));

        addCommand(new PlaceQueue(LOGGER));
        addCommand(new PlaceDelete(LOGGER));
        addCommand(new PlaceGetFile(LOGGER));
        addCommand(new PlaceViewQueue(LOGGER));
    }

    private void addCommand(CommandInterface cmd) {
        boolean nameFound = commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    public List<CommandInterface> getCommands() {
        return commands;
    }

    @Nullable
    public CommandInterface getCommand(String search) {
        String searchLowerCase = search.toLowerCase();

        for (CommandInterface cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(searchLowerCase) || cmd.getAliases().contains(searchLowerCase)) {
                return cmd;
            }
        }

        return null;
    }

    public void handle(GuildMessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(CONFIG.Prefix.get()), "").split("\\s+");

        String invoke = split[0].toLowerCase();
        CommandInterface cmd = this.getCommand(invoke);
        List<String> arguments = Arrays.asList(split).subList(1, split.length);
        CommandContext ctx = new CommandContext(event, arguments);

        EmbedHelper.deleteMsg(ctx.getMessage(), 128);

        if (cmd != null && PermissionManager.permissionCheck(ctx, getCommand(invoke))) {
            if (!cmd.requiresFurtherChecks()) {
                commandLogger(cmd.getName(), ctx, true);
            }

            cmd.handle(ctx);
        } else {
            commandLogger(invoke, ctx, false);
        }
    }

    public static void commandLogger(String name, CommandContext ctx, boolean success) {
        if (success) {
            ctx.getMessage().addReaction(EMOTES.RDG.getAsReaction()).queue(
                    null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)
            );
        } else {
            ctx.getMessage().addReaction(EMOTES.RDR.getAsReaction()).queue(
                    null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)
            );
        }

        cmdLogger.info(ctx.getAuthor().getName() + " ran command " + CONFIG.Prefix.get() + name.toLowerCase() +
                (ctx.getArguments().size() != 0 ? " " + ctx.getArguments().toString() : "") +
                (success ? " successfully" : " unsuccessfully"));
    }
}
