package commandHandling.commands.modCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;

import java.util.List;

import static services.listeners.CountThread10kPolicingListener.updateLastCountedNumber;

public class SetCountProgress implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Channel.class);

    public SetCountProgress(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        int setProgressTo;
        int oldProgress;

        try {
            setProgressTo = Integer.parseInt(ctx.getArguments().get(0));
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        oldProgress = updateLastCountedNumber(setProgressTo);

        LOGGER.info(ctx.getAuthor().getAsTag() + " updated the last counter number from " + oldProgress + " to " + setProgressTo);
    }

    @Override
    public String getName() {
        return "SetCountProgress";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Set the progress of <#993390913881640970>");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("scp");
    }

    @Override
    public int getRestrictionLevel() {
        return 2;
    }
}
