package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.PermissionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static services.PermissionManager.getSnowflakes;
import static services.database.DBHandlerSnowflakePermissions.addSnowflakePermissions;
import static services.database.DBHandlerSnowflakePermissions.removeSnowflakePermissions;
import static services.discordHelpers.EmbedHelper.embedBuilder;
import static services.discordHelpers.EmbedHelper.sendEmbed;
import static services.discordHelpers.ReactionHelper.addReaction;

public class SnowflakePermission implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(SnowflakePermission.class);

    public SnowflakePermission(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> snowflakes = getSnowflakes();

        if (ctx.getArguments().size() == 4) {
            String discordUserId = ctx.getArguments().get(0).replace("<@", "").replace(">", "");
            String discordServerId = ctx.getGuild().getId();
            String discordChannelId = ctx.getArguments().get(1).replace("<#", "").replace(">", "");
            String command = ctx.getArguments().get(2);

            if (snowFlakeCheck(snowflakes, discordUserId, discordServerId, discordChannelId, command)) {
                removeSnowflakePermissions(discordUserId, discordServerId, discordChannelId, command);
            } else {
                if (discordChannelId.equals("e")) {
                    for (TextChannel channel : ctx.getGuild().getTextChannels()) {
                        if (ctx.getSelfMember().getPermissions(channel).contains(Permission.MESSAGE_EMBED_LINKS) &&
                                ctx.getSelfMember().getPermissions(channel).contains(Permission.MESSAGE_WRITE)) {
                            addSnowflakePermissions(discordUserId, discordServerId, channel.getId(), command);
                        }
                    }
                } else {
                    addSnowflakePermissions(discordUserId, discordServerId, discordChannelId, command);
                }
            }
            addReaction(ctx, 0);
            PermissionManager.reload();
        } else if (ctx.getArguments().size() == 0){
            EmbedBuilder embed = embedBuilder("Special Snowflakes");

            for (String discordUserId : snowflakes.keySet()) {
                StringBuilder sb = new StringBuilder();

                for (String discordServerId : snowflakes.get(discordUserId).keySet()) {

                    for (String discordChannelId : snowflakes.get(discordUserId).get(discordServerId).keySet()) {
                        String channelName = ctx.getJDA().getGuildById(discordServerId).getTextChannelById(discordChannelId).getAsMention();

                        for (String command : snowflakes.get(discordUserId).get(discordServerId).get(discordChannelId)) {
                            sb.append(channelName).append(" - ").append(command).append("\n");
                        }
                    }
                }
                User user = ctx.getJDA().retrieveUserById(discordUserId).complete();
                embed.addField(user.getAsTag(), sb.toString(), false);
            }

            sendEmbed(ctx, embed, 32);
        } else {
            BotExceptions.invalidArgumentsException(ctx);
        }
    }

    private boolean snowFlakeCheck(HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> snowflakes,
                                   String discordUserid, String discordServerId, String discordChannelId, String command) {
        return snowflakes.containsKey(discordUserid) && snowflakes.get(discordUserid).containsKey(discordServerId) &&
                snowflakes.get(discordUserid).get(discordServerId).containsKey(discordChannelId) &&
                snowflakes.get(discordUserid).get(discordServerId).get(discordChannelId).contains(command);
    }

    @Override
    public String getName() {
        return "SnowflakePermission";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("sfp");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }

    @Override
    public boolean requiresFurtherChecks() {
        return true;
    }
}
