package commandhandling.commands.mod;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.PermissionManager;
import services.database.daos.UsersDAO;
import services.discordhelpers.EmbedHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class BlackList implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:(?:<@)?\\d{18,19}>?\\s?)?\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackList.class);

    @Override
    public void handle(CommandContext ctx) {
        UsersDAO usersDAO = new UsersDAO();
        ArrayList<String> blacklist = usersDAO.getUserBlacklist();

        if (!ctx.getArguments().isEmpty()) {
            String id = ctx.getArguments().get(0).replaceAll("[<@>]", "");

            usersDAO.toggleUserBlacklist(id);
            String logMessage = (blacklist.contains(id) ? "Removed" : "Added") + "user `{}` from the blacklist";
            LOGGER.info(logMessage, Objects.requireNonNull(ctx.getJDA().getUserById(id)).getName());

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