package commandhandling.commands.pleb;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import assets.Config;
import services.BotExceptions;
import services.CommandManager;
import services.discordhelpers.EmbedHelper;
import assets.objects.HelpEntry;
import services.discordhelpers.ReactionHelper;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class Help implements CommandInterface {
    private static Pattern argumentPattern = null;
    private final CommandManager manager;

    public Help(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        String prefix = Config.prefix;

        if (ctx.getArguments().isEmpty() || ctx.getMessage().getContentRaw().contains(Config.prefix + " ")) {
            EmbedBuilder embed = EmbedHelper.embedBuilder("Help");

            HashMap<String, HelpEntry> commandGroups = new HashMap<>();
            PriorityQueue<HelpEntry> sortedCommandGroups = new PriorityQueue<>();

            for (CommandInterface cmd : manager.getCommands()) {
                String[] hierarchySplit = cmd.getClass().getName().split("\\.");
                String groupName = hierarchySplit[hierarchySplit.length - 2];

                if (ctx.getSecurityClearance() <= cmd.getRestrictionLevel()) {
                    if (!commandGroups.containsKey(groupName)) {
                        commandGroups.put(groupName, new HelpEntry(0, groupName, new StringBuilder()));
                    }
                    commandGroups.get(groupName).lines++;
                    commandGroups.get(groupName).commands.append(cmd.getName()).append("\n");
                }
            }

            for (String key : commandGroups.keySet()) {
                sortedCommandGroups.add(commandGroups.get(key));
            }

            while (!sortedCommandGroups.isEmpty()) {
                HelpEntry e = sortedCommandGroups.poll();
                embed.addField("__" + e.group + "__", "```\n" + e.commands + "```", true);
            }

            for (int i = commandGroups.size() % 3; i < 3 && i != 0; i++) {
                embed.addBlankField(true);
            }

            embed.setFooter(Config.prefix + "help <command> gives you a more detailed description" +
                    (ctx.getSecurityClearance() < 3 ? "\nAppending '-p' prevents messages from being deleted" : ""));

            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
            sendMessage(ctx, mca, 64);
        } else {
            CommandInterface command = CommandManager.getCommand(ctx.getArguments().get(0));

            if (command == null) {
                BotExceptions.commandNotFoundException(ctx, ctx.getArguments().get(0));
                ReactionHelper.addReaction(ctx, 5);
                return;
            }

            StringBuilder aliases = new StringBuilder();
            for (String s : command.getAliases()) {
                if (aliases.toString().isEmpty()) {
                    aliases.append(prefix).append(s);
                } else {
                    aliases.append(", ").append(prefix).append(s);
                }
            }

            EmbedBuilder embed = command.getHelp();
            embed.setTitle("Help - " + command.getName());
            embed.setColor(new Color(0xb074ad));
            if (!aliases.isEmpty()) {
                embed.addField("__Aliases__", "```" + aliases + "```", false);
            }

            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
            sendMessage(ctx, mca, 64);
        }
            ReactionHelper.addReaction(ctx, 0);
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
        return List.of("commands", "cmds", "");
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        if (argumentPattern == null) {
            StringBuilder sb = new StringBuilder();

            for (CommandInterface ci : manager.getCommands()) {
                sb.append(ci.getNameLC()).append("|");

                for (String alias : ci.getAliases()) {
                    sb.append(alias).append("|");
                }
            }

            sb.deleteCharAt(sb.length() - 1);

            argumentPattern = Pattern.compile("^(?:" + sb + ")?\\s?$");
        }

        return argumentPattern.matcher(args).matches();
    }
}
