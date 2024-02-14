package commandhandling.commands.owner;

import assets.Config;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.CommandManager;
import services.PermissionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static services.PermissionManager.getSnowflakes;
import static services.database.DBHandlerSnowflakePermissions.addSnowflakePermissions;
import static services.database.DBHandlerSnowflakePermissions.removeSnowflakePermissions;
import static services.discordhelpers.EmbedHelper.embedBuilder;
import static services.discordhelpers.EmbedHelper.sendEmbed;
import static services.discordhelpers.ReactionHelper.addReaction;

public class SnowflakePermission implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(SnowflakePermission.class);
    private final CommandManager cm;

    public SnowflakePermission(CommandManager cm) {
        this.cm = cm;
    }

    @Override
    public void handle(CommandContext ctx) {
        HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> snowflakes = getSnowflakes();

        if (ctx.getArguments().isEmpty()){
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

            sendEmbed(ctx, embed, 64);
        } else {
            String discordChannelId, discordServerId, discordUserId, command;
            int idx;

            if (ctx.getArguments().contains("-s")) {
                idx = ctx.getArguments().indexOf("-s") + 1;
                discordServerId = ctx.getArguments().get(idx);
            }  else {
                discordServerId = ctx.getGuild().getId();
            }

            if (ctx.getArguments().contains("-c")) {
                idx = ctx.getArguments().indexOf("-c") + 1;
                discordChannelId = ctx.getArguments().get(idx).replaceAll("[<#>]", "");
            }  else {
                discordChannelId = ctx.getChannel().getId();
            }

            try {
                idx = ctx.getArguments().indexOf("-u") + 1;
                discordUserId = ctx.getArguments().get(idx).replaceAll("[<@>]", "");

                idx = ctx.getArguments().indexOf("-cmd") + 1;
                command = ctx.getArguments().get(idx);

                if (ctx.getJDA().retrieveUserById(discordUserId).complete() == null) {
                    throw new IllegalArgumentException();
                }

                if (!CommandManager.isCommand(command)) {
                    throw new IllegalArgumentException();
                }

                if (!PermissionManager.getWhitelistedServers().contains(discordServerId)) {
                    throw new IllegalArgumentException();
                }

                if (ctx.getJDA().getGuildById(discordServerId).getTextChannelById(discordChannelId) == null) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }

            if (snowFlakeCheck(snowflakes, discordUserId, discordServerId, discordChannelId, command)) {
                removeSnowflakePermissions(discordUserId, discordServerId, discordChannelId, command);
            } else {
                addSnowflakePermissions(discordUserId, discordServerId, discordChannelId, command);
            }

            PermissionManager.reload();
        }

        addReaction(ctx, 0);
    }

    private boolean snowFlakeCheck(HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> snowflakes, String discordUserid, String discordServerId, String discordChannelId, String command) {
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
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Give permissions to some special snowflake which they are usually not meant to have");
        embed.addField("__Usage__", "```" + Config.prefix + getName() + " [<-s> <Server ID>] [<-c> <Channel ID>] <-u> <User> <-cmd> <Command>```", false);
        embed.addField("__Server ID__", "Specify the server which the channel is in, defaults to current server", false);
        embed.addField("__<Channel ID>__", "Specify the channel where the command should be usable, defaults to current channel", false);
        embed.addField("__<User>__", "Specify who to give the perms to. Can be ping or user ID", false);
        embed.addField("__<User>__", "Specify the command to be made available", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("sfp", "snowflake");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        String regex = "^(?:-s\\s\\d{18,19}\\s)?(?:-c\\s(?:<#)?\\d{18,19}>?\\s)?(?:-u\\s(?:<@)?\\d{18,19}>?\\s)-cmd\\s\\w+\\s?";
        StringBuilder sb = new StringBuilder();

        for (CommandInterface ci : cm.getCommands()) {
            if (ci.getRestrictionLevel() < 3) {
                sb.append(ci.getNameLC()).append("|");
            }
        }

        sb.deleteCharAt(sb.length() - 1);

        Pattern argumentPattern = Pattern.compile(regex + "(?:" + sb + ")?$");
        return argumentPattern.matcher(args).matches();
    }
}
