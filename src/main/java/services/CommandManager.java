package services;

import assets.Config;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import commandhandling.commands.coursereview.Course;
import commandhandling.commands.coursereview.CourseReviewStats;
import commandhandling.commands.coursereview.CourseReviewVerify;
import commandhandling.commands.admin.LockDown;
import commandhandling.commands.admin.Nuke;
import commandhandling.commands.admin.Watch;
import commandhandling.commands.mod.BlackList;
import commandhandling.commands.mod.Channel;
import commandhandling.commands.owner.*;
import commandhandling.commands.place.*;
import commandhandling.commands.pleb.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageDeleteHelper.deleteMsg;
import static services.discordhelpers.ReactionHelper.addReaction;
import static services.logging.LoggingHelper.commandLogger;

public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private static final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new About(LOGGER));
        addCommand(new BlackList(LOGGER));
        addCommand(new Channel(this, LOGGER));
        addCommand(new Course(LOGGER));
        addCommand(new CourseReviewVerify(LOGGER));
        addCommand(new Delete(LOGGER));
        addCommand(new Ducky(LOGGER));
        addCommand(new SnowflakePermission(LOGGER));
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
        addCommand(new SQL(LOGGER));
        addCommand(new Status(LOGGER));
        addCommand(new GetAPILog(LOGGER));
        addCommand(new Watch(LOGGER));
        addCommand(new CourseReviewStats(LOGGER));

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
        addCommand(new PlaceVerify(LOGGER));
        addCommand(new ChannelEfficiency(LOGGER));
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

    public static CommandInterface getCommand(String search) {
        String searchLowerCase = search.toLowerCase();

        for (CommandInterface cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(searchLowerCase) || cmd.getAliases().contains(searchLowerCase)) {
                return cmd;
            }
        }

        return null;
    }

    public void handle(MessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.prefix), "").split("\\s+");

        String invoke = split[0].toLowerCase();
        CommandInterface cmd = this.getCommand(invoke);
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(split).subList(1, split.length));
        CommandContext ctx = new CommandContext(event, arguments);

        if (arguments.contains("--persist")) {
            ctx.setPersistent();
        }

        deleteMsg(ctx.getMessage(), 128);
        commandLogger(ctx);

        if (cmd != null) {
            if (PermissionManager.permissionCheck(ctx, getCommand(invoke))) {
                (new Thread(() -> cmd.handle(ctx))).start();
                if (!getCommand(invoke).requiresFurtherChecks()) {
                    addReaction(ctx, 0);
                }
            }
        } else {
            addReaction(ctx, 5);
        }
    }

    public static boolean isCommand(String command) {
        return getCommand(command) != null;
    }
}
