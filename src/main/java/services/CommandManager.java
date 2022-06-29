package services;

import assets.CONFIG;
import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import commandHandling.commands.CourseReview.Course;
import commandHandling.commands.CourseReview.CourseReviewVerify;
import commandHandling.commands.adminCommands.LockDown;
import commandHandling.commands.adminCommands.Nuke;
import commandHandling.commands.modCommands.BlackList;
import commandHandling.commands.modCommands.Channel;
import commandHandling.commands.ownerCommands.*;
import commandHandling.commands.place.*;
import commandHandling.commands.publicCommands.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static services.discordHelpers.MessageDeleteHelper.deleteMsg;
import static services.discordHelpers.ReactionHelper.addReaction;
import static services.logging.LoggingHelper.commandLogger;

public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new About(LOGGER));
        addCommand(new Avatar(LOGGER));
        addCommand(new BlackList(LOGGER));
        addCommand(new Channel(this, LOGGER));
        addCommand(new Course(LOGGER));
        addCommand(new CourseReviewVerify(LOGGER));
        addCommand(new Delete(LOGGER));
        addCommand(new Ducky(LOGGER));
        addCommand(new SnowflakePermission(LOGGER));
//        addCommand(new ExportDatabase(LOGGER));
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
        addCommand(new PlaceView(LOGGER));
        addCommand(new PlaceStatus(LOGGER));
        addCommand(new PlacePreview(LOGGER));
        addCommand(new PlaceEncode(LOGGER));
        addCommand(new PlaceDraw(LOGGER));
        addCommand(new PlaceStop(LOGGER));
        addCommand(new PlaceStopQueue(LOGGER));
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

        deleteMsg(ctx.getMessage(), 128);
        commandLogger(ctx);

        if (cmd != null && PermissionManager.permissionCheck(ctx, getCommand(invoke))) {
            (new Thread(() -> cmd.handle(ctx))).start();
            if (!getCommand(invoke).requiresFurtherChecks()) {
                addReaction(ctx, 0);
            }
        }
    }
}
