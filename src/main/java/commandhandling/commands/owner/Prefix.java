package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Config;
import services.BotExceptions;
import services.database.DBHandlerConfig;

import java.util.regex.Pattern;

import static services.discordhelpers.ReactionHelper.addReaction;

public class Prefix implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Prefix.class);
    public static final Pattern argumentPattern = Pattern.compile("^\\S+$");

    @Override
    public void handle(CommandContext ctx) {
        try {
            DBHandlerConfig.updateConfig("prefix", ctx.getArguments().get(0));
            addReaction(ctx, 0);
            Config.reload();
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
        }
    }

    @Override
    public String getName() {
        return "Prefix";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Changes the prefix");
        return embed;
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
