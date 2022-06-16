package commandHandling.commands.publicCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.BotExceptions;
import services.CommandManager;
import services.logging.EmbedHelper;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Help implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Help.class);
    private final CommandManager manager;

    public Help(CommandManager manager, Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        String prefix = CONFIG.Prefix.get();

        if (ctx.getArguments().isEmpty() || ctx.getMessage().getContentRaw().contains(CONFIG.Prefix.get() + " ")) {
            EmbedBuilder embed = EmbedHelper.embedBuilder("Help");

            HashMap<String, Entry> commandGroups = new HashMap<>();
            PriorityQueue<Entry> sortedCommandGroups = new PriorityQueue<>();

            for (CommandInterface cmd : manager.getCommands()) {
                String[] hierarchySplit = cmd.getClass().getName().split("\\.");
                String groupName = hierarchySplit[hierarchySplit.length - 2];

                if (ctx.getSecurityClearance() <= cmd.getRestrictionLevel()) {
                    if (!commandGroups.containsKey(groupName)) {
                        commandGroups.put(groupName, new Entry(0, groupName, new StringBuilder()));
                    }
                    commandGroups.get(groupName).lines++;
                    commandGroups.get(groupName).commands.append(cmd.getName()).append("\n");
                }
            }

            for (String key : commandGroups.keySet()) {
                sortedCommandGroups.add(commandGroups.get(key));
            }

            while (!sortedCommandGroups.isEmpty()) {
                Entry e = sortedCommandGroups.poll();
                embed.addField("__" + e.group + "__", "```\n" + e.commands + "```", true);
            }

            for (int i = commandGroups.size() % 3; i < 3 && i != 0; i++) {
                embed.addBlankField(true);
            }

            embed.setFooter(CONFIG.Prefix.get() + "help <command> gives you a more detailed description");

            EmbedHelper.sendEmbed(ctx, embed, 64);
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

            EmbedHelper.sendEmbed(ctx, embed, 64);
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

class Entry implements Comparable<Entry> {
    public int lines;
    public String group;
    public StringBuilder commands;

    public Entry(int lines, String group, StringBuilder commands) {
        this.lines = lines;
        this.group = group;
        this.commands = commands;
    }

    @Override
    public int compareTo(@NotNull Entry o) {
        return lines == o.lines ? 0 : o.lines - lines;
    }
}