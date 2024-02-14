package commandhandling.commands.admin;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.listeners.CountThreadListener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static services.database.DBHandlerConfig.updateConfig;

public class Watch implements CommandInterface {
    public static final Pattern argumentPattern = Pattern.compile("^(?:<@)?\\d{18}>?\\s?$");
    private final Logger LOGGER = LoggerFactory.getLogger(Nuke.class);

    @Override
    public void handle(CommandContext ctx) {
        String tempId = ctx.getArguments().get(0).replaceAll("[<@>]", "");

        CountThreadListener.listenTo = tempId;
        CountThreadListener.checkRecentMessages();

        updateConfig("CountThreadListenTo", tempId);

        LOGGER.info(ctx.getAuthor().getAsTag() + " changed the follow ID to " + tempId);

        ctx.getChannel().sendMessage("I am watching you <@" + tempId + "> <:bustinGood:747783377171644417>").queue(
                (msg) -> msg.delete().queueAfter(60, TimeUnit.SECONDS)
        );
    }

    @Override
    public String getName() {
        return "Watch";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Changes who I wait for in <#996746797236105236>");
        return embed;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
