package services;

import assets.Config;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
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
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordhelpers.MessageDeleteHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static services.discordhelpers.ReactionHelper.addReaction;
import static services.logging.LoggingHelper.commandLogger;

public class CommandManager {
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private static final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager() {
        // owner
        addCommand(new Delete());
        addCommand(new GetAPILog());
        addCommand(new Kill());
        addCommand(new NickName());
        addCommand(new Prefix());
        addCommand(new ProfilePicture());
        addCommand(new Purge());
        addCommand(new Say());
        addCommand(new Scrape());
        addCommand(new Servers());
        addCommand(new SnowflakePermission(this));
        addCommand(new Status());

        // admin
        addCommand(new LockDown());
        addCommand(new Nuke());
        addCommand(new Watch());

        // mod
        addCommand(new BlackList());
        addCommand(new Channel(this));

        // pleb
        addCommand(new About());
        addCommand(new ChannelEfficiency());
        addCommand(new Ducky());
        addCommand(new Help(this));
        addCommand(new LetMeGoogleThatForYou());
        addCommand(new Ping());
        addCommand(new PurgeDMs());

        // coursereview
        addCommand(new CourseReviewStats());
        addCommand(new CourseReviewVerify());

        // place
        addCommand(new PlaceDelete());
        addCommand(new PlaceDraw());
        addCommand(new PlaceEncode());
        addCommand(new PlaceGetFile());
        addCommand(new PlacePreview());
        addCommand(new PlaceQueue());
        addCommand(new PlaceStatus());
        addCommand(new PlaceStop());
        addCommand(new PlaceStopQueue());
        addCommand(new PlaceVerify());
        addCommand(new PlaceView());
        addCommand(new PlaceViewQueue());

        LOGGER.info(commands.size() + " commands loaded");
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
        String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(Config.prefix), "").split("\\s+");

        String invoke = split[0].toLowerCase();
        CommandInterface cmd = getCommand(invoke);

        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(split).subList(1, split.length));
        CommandContext ctx = new CommandContext(event, arguments);

        StringBuilder argRegexCheck = new StringBuilder();
        arguments.forEach((it) -> argRegexCheck.append(it).append(" "));

        commandLogger(ctx);

        if (cmd != null && cmd.argumentCheck(argRegexCheck) && cmd.attachmentCheck(ctx)) {
            MessageDeleteHelper.deleteMessage(ctx.getMessage(), cmd.deleteAfter());
            if (PermissionManager.permissionCheck(ctx, cmd)) {
                    executorService.submit(() -> {
                            try {
                                cmd.handle(ctx);
                            } catch (InsufficientPermissionException ipe) {
                                LOGGER.error("Bot is missing permissions", ipe);
                            }
                    });
                addReaction(ctx, 0);
            }
        } else {
            MessageDeleteHelper.deleteMessage(ctx.getMessage(), 128);
            addReaction(ctx, 5);
        }
    }
}
