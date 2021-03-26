package commandHandling.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import resources.CONFIG;
import services.*;
import commandHandling.CommandContext;
import commandHandling.CommandInterface;

import java.util.List;

public class Help implements CommandInterface {
    private final CommandManager manager;

    public Help(CommandManager manager, Logger LOGGER) {
        this.manager = manager;
        LOGGER.info("Loaded Command Help");
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> arguments = ctx.getArguments();
        TextChannel channel = ctx.getChannel();

        if (arguments.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            builder.append("List of commands\n");

            manager.getCommands().stream().map(CommandInterface::getName).forEach(
                    (it) -> builder.append("`").append(CONFIG.Prefix.get()).append(it).append("`\n")
            );

            channel.sendMessage(builder.toString()).queue();
            return;
        }

        String search = arguments.get(0);
        CommandInterface command = manager.getCommand(search);

        if (command == null) {
            channel.sendMessage("Nothing found for " + search).queue();
            return;
        }

        channel.sendMessage(command.getHelp()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "shows a list of commands \n" +
                "Usage: `rdhelp [command]`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "cmds", "commandlist");
    }
}