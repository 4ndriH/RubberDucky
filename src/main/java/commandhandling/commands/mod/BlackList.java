package commandhandling.commands.mod;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.PermissionManager;
import services.database.DBHandlerBlacklistedUsers;
import services.discordhelpers.EmbedHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static services.PermissionManager.getBlacklist;

public class BlackList implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(BlackList.class);
    public final Pattern argumentPattern = Pattern.compile("^(?:(?:<@)?\\d{18}>?\\s?)?$");

    @Override
    public void handle(CommandContext ctx) {
        ArrayList<String> blacklist = getBlacklist();

        if (!ctx.getArguments().isEmpty()) {
            String id = ctx.getArguments().get(0).replaceAll("[<@>]", "");
            if (blacklist.contains(id)) {
                DBHandlerBlacklistedUsers.removeUserFromBlacklist(id);
            } else {
                DBHandlerBlacklistedUsers.addUserToBlacklist(id);
            }
            PermissionManager.reload();
        } else {
            ArrayList<String> ids = blacklist;
            EmbedBuilder embed = EmbedHelper.embedBuilder("Blacklisted people");

            if (ids.isEmpty()) {
                embed.setDescription("-");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String id : ids) {
                    sb.append("<@!").append(id).append(">\n");
                }
                Message msg = ctx.getChannel().sendMessage("beep boop").complete();
                msg.editMessage(sb.toString()).complete();
                msg.delete().queue();
                embed.setDescription(sb.toString());
            }

            EmbedHelper.sendEmbed(ctx, embed, 32);
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