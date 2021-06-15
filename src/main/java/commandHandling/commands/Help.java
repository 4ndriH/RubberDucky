package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import services.BotExceptions;
import services.CommandManager;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Help implements CommandInterface {
    private final CommandManager manager;

    public Help(CommandManager manager, Logger LOGGER) {
        this.manager = manager;
        LOGGER.info("Loaded Command Help");
    }

    @Override
    public void handle(CommandContext ctx) {
        services.Logger.command(ctx, "help", true);
        List<String> arguments = ctx.getArguments();
        TextChannel channel = ctx.getChannel();

        if (arguments.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("Help");
            embed.setColor(new Color(0xb074ad));
            embed.setFooter("rdhelp <command> gives you a more detailed description");

            embed.addField("__Miscellaneous__", "rdhelp\nrdping\nrdspokesPeople\nrdplace\nrdgalactic", true);
            embed.addField("__Owner__", "rdkill\nrdshutdown\nrddelete\nrdpurge\nrdblacklist\nrdservers\nrdchannel\nrdlockdown\nrdprefix", true);

            ctx.getChannel().sendMessageEmbeds(embed.build()).queue(msg ->
                    msg.delete().queueAfter(64, TimeUnit.SECONDS));
            return;
        }

        String search = arguments.get(0);
        CommandInterface command = manager.getCommand(search);

        if (command == null) {
            BotExceptions.commandNotFoundException(ctx, search);
            return;
        }

        channel.sendMessageEmbeds(command.getHelp().build()).queue(msg ->
                msg.delete().queueAfter(64, TimeUnit.SECONDS));
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Help");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Returns a list of all available commands");
        embed.setFooter("I mean what did you expect?");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "cmds", "commandlist");
    }
}