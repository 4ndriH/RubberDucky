package services;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import commandHandling.commands.Shutdown;
import commandHandling.commands.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new Shutdown(LOGGER));
        addCommand(new Kill(LOGGER));
        addCommand(new Ping(LOGGER));
        addCommand(new Help(this, LOGGER));
        addCommand(new Place(LOGGER));
        addCommand(new SpokesPeople(LOGGER));
        addCommand(new Galactic(LOGGER));
        addCommand(new Delete(LOGGER));
        addCommand(new Purge(LOGGER));
        addCommand(new BlackList(LOGGER));
        addCommand(new Servers(LOGGER));
        addCommand(new Channel(LOGGER, this));
        addCommand(new LockDown(LOGGER));
        addCommand(new Prefix(LOGGER));
        addCommand(new NickName(LOGGER));
        addCommand(new ProfilePicture(LOGGER));
        addCommand(new Status(LOGGER));
        addCommand(new Say(LOGGER));
        addCommand(new Temp());
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

        Miscellaneous.deleteMsg(ctx.getMessage(), 128);

        if (cmd != null && PermissionManager.permissionCheck(ctx, cmd.getName(), this)) {
            try {
                cmd.handle(ctx);
            } catch (Exception e) {
                DiscordLogger.exception(ctx, e);
            }
        } else {
            DiscordLogger.command(ctx, invoke, false);
        }
    }
}
