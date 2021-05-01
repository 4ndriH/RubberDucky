package services;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import commandHandling.commands.Help;
import commandHandling.commands.Ping;
import commandHandling.commands.Place;
import commandHandling.commands.SemesterSpokesPeople;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new Ping(LOGGER));
        addCommand(new Help(this, LOGGER));
        addCommand(new Place(LOGGER));
        addCommand(new SemesterSpokesPeople());
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
            if (cmd.getName().equals(searchLowerCase) || cmd.getAliases().contains(searchLowerCase)) {
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

        ctx.getMessage().delete().onErrorFlatMap(
                error -> ctx.getJDA().getGuildById("817850050013036605").getTextChannelById("817850050013036608")
                        .sendTyping()
        ).queueAfter(128, TimeUnit.SECONDS);

        if (cmd != null && PermissionManager.permissionCheck(ctx, invoke)) {
            LOGGER.info(event.getAuthor() + " running command " + invoke);
            CommandReaction.success(ctx);
            cmd.handle(ctx);
        } else {
            CommandReaction.fail(ctx);
        }
    }
}

