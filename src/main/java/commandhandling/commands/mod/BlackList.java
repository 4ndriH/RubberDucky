package commandhandling.commands.mod;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.PermissionManager;
import services.database.DBHandlerBlacklistedUsers;
import services.discordhelpers.EmbedHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static services.PermissionManager.getBlacklist;
import static services.discordhelpers.MessageSendHelper.sendMessage;

public class BlackList implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:(?:<@)?\\d{18,19}>?\\s?)?\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackList.class);

    @Override
    public void handle(CommandContext ctx) {
        ArrayList<String> blacklist = getBlacklist();

        if (!ctx.getArguments().isEmpty()) {
            String id = ctx.getArguments().get(0).replaceAll("[<@>]", "");
            if (blacklist.contains(id)) {
                DBHandlerBlacklistedUsers.removeUserFromBlacklist(id);
                LOGGER.info("Removed user `" + Objects.requireNonNull(ctx.getJDA().getUserById(id)).getName() + "` from the blacklist");
            } else {
                DBHandlerBlacklistedUsers.addUserToBlacklist(id);
                LOGGER.info("Added user `" + Objects.requireNonNull(ctx.getJDA().getUserById(id)).getName() + "` from the blacklist");
            }
            PermissionManager.reload();
        } else {
            EmbedBuilder embed = EmbedHelper.embedBuilder("Blacklisted people");

            if (blacklist.isEmpty()) {
                embed.setDescription("-");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String id : blacklist) {
                    sb.append("<@!").append(id).append(">\n");
                }
                Message msg = ctx.getChannel().sendMessage("beep boop").complete();
                msg.editMessage(sb.toString()).complete();
                msg.delete().queue();
                embed.setDescription(sb.toString());
            }

            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
            sendMessage(ctx, mca, 32);
        }
    }

    @Override
    public String getName() {
        return "Blacklist";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Allows me to blacklist your annoying ass");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("bl");
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}