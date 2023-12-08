package commandhandling.commands.admin;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.listeners.CountThreadListener;

import java.util.concurrent.TimeUnit;

import static services.database.DBHandlerConfig.updateConfig;
import static services.discordhelpers.ReactionHelper.addReaction;

public class Watch implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Nuke.class);

    public Watch(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        String tempId = ctx.getMessage().getContentRaw().replaceAll("\\D", "");

        if (!StringUtils.isNumeric(tempId) || tempId.length() < 18) {
            addReaction(ctx, 5);
            return;
        }

        addReaction(ctx, 0);

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
    public int getRestrictionLevel() {
        return 1;
    }

    @Override
    public boolean requiresFurtherChecks() {
        return true;
    }
}
