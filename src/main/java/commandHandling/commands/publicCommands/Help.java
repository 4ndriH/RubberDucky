package commandHandling.commands.publicCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.BotExceptions;
import services.CommandManager;
import services.Miscellaneous;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class Help implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Help.class);
    private final CommandManager manager;

    public Help(CommandManager manager, Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);
        String prefix = CONFIG.Prefix.get();

        if (ctx.getArguments().isEmpty()) {
            EmbedBuilder embed = Miscellaneous.embedBuilder("Help");

            HashMap<String, StringBuilder> commandGroups = new HashMap<>();

            for (CommandInterface cmd : manager.getCommands()) {
                String[] hierarchySplit = cmd.getClass().getName().split("\\.");
                String groupName = hierarchySplit[hierarchySplit.length - 2];

                if (ctx.getSecurityClearance() <= cmd.getRestrictionLevel()) {
                    if (!commandGroups.containsKey(groupName)) {
                        commandGroups.put(groupName, new StringBuilder());
                    }
                    commandGroups.get(groupName).append(cmd.getName()).append("\n");
                }
            }

            for (String key : commandGroups.keySet()) {
                embed.addField("__" + key + "__", "```\n" + commandGroups.get(key) + "```", true);
            }

            for (int i = commandGroups.size() % 3; i < 3 && i != 0; i++) {
                embed.addBlankField(true);
            }

            embed.setFooter(CONFIG.Prefix.get() + "help <command> gives you a more detailed description");

            ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                    msg ->Miscellaneous.deleteMsg(msg, 64)
            );
        } else {
            CommandInterface command = manager.getCommand(ctx.getArguments().get(0));

            if (command == null) {
                BotExceptions.commandNotFoundException(ctx, ctx.getArguments().get(0));
                return;
            }

            StringBuilder aliases = new StringBuilder();
            for (String s : command.getAliases()) {
                if (aliases.toString().length() == 0) {
                    aliases.append(prefix).append(s);
                } else {
                    aliases.append(", ").append(prefix).append(s);
                }
            }

            EmbedBuilder embed = command.getHelp();
            embed.setTitle("Help - " + command.getName());
            embed.setColor(new Color(0xb074ad));
            if (aliases.length() != 0) {
                embed.addField("__Aliases__", "```" + aliases + "```", false);
            }

            ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                    msg -> Miscellaneous.deleteMsg(msg, 64)
            );
        }
    }

    @Override
    public String getName() {
        return "Help";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns a list of all available commands");
        embed.setFooter("I mean what did you expect?");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "cmds", "commandlist", "");
    }
}