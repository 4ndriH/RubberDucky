package commandhandling.commands.admin;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;

import java.util.regex.Pattern;

public class Nuke implements CommandInterface {
    public static final Pattern argumentPattern = Pattern.compile("^\\d+\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(Nuke.class);

    @Override
    public void handle(CommandContext ctx) {
        int nr = 1;

        try {
            nr += Integer.parseInt(ctx.getArguments().get(0));
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        LOGGER.debug("Nuking " + nr + " messages in " + ctx.getChannel().getName());

        ctx.getChannel().getIterableHistory().takeAsync(nr).thenAccept(ctx.getChannel()::purgeMessages);
    }

    @Override
    public String getName() {
        return "Nuke";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Nukes the specified amount of messages");
        return embed;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
