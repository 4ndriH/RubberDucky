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
            StringBuilder publicCMDs = new StringBuilder();
            StringBuilder ownerCMDs = new StringBuilder();


            for (CommandInterface cmd : manager.getCommands()) {
                if (cmd.isOwnerOnly()) {
                    ownerCMDs.append(prefix).append(cmd.getName().toLowerCase()).append("\n");
                } else {
                    publicCMDs.append(prefix).append(cmd.getName().toLowerCase()).append("\n");
                }
            }

            embed.addField("__Miscellaneous__", "```\n" + publicCMDs + "```", true);
            embed.addField("__Owner__", "```\n" + ownerCMDs + "```", true);
            embed.setFooter("rdhelp <command> gives you a more detailed description");

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