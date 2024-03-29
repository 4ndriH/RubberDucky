package commandhandling.commands.admin;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.listeners.CountThreadListener;

import java.util.regex.Pattern;

import static services.database.DBHandlerConfig.updateConfig;
import static services.discordhelpers.MessageSendHelper.sendMessage;

public class Watch implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:<@)?\\d{18,19}>?\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(Watch.class);

    @Override
    public void handle(CommandContext ctx) {
        String tempId = ctx.getArguments().get(0).replaceAll("[<@>]", "");

        CountThreadListener.listenTo = tempId;
        CountThreadListener.checkRecentMessages();

        updateConfig("CountThreadListenTo", tempId);

        LOGGER.info(ctx.getAuthor().getAsTag() + " changed the follow ID to " + tempId);

        MessageCreateAction mca = ctx.getChannel().sendMessage("I am watching you <@" + tempId + "> <:bustinGood:747783377171644417>");
        sendMessage(ctx, mca, 64);
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
