package commandhandling.commands.owner;

import assets.Config;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.CommandManager;
import services.PermissionManager;
import services.database.daos.UsersDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static services.discordhelpers.EmbedHelper.embedBuilder;
import static services.discordhelpers.MessageSendHelper.sendMessage;
import static services.discordhelpers.ReactionHelper.addReaction;

public class SnowflakePermission implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakePermission.class);
    private static  Pattern argumentPattern = null;
    private final CommandManager cm;

    public SnowflakePermission(CommandManager cm) {
        this.cm = cm;
    }

    @Override
    public void handle(CommandContext ctx) {
        UsersDAO usersDAO = new UsersDAO();
        HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> snowflakes = usersDAO.getSnowflakePermissions();

        if (ctx.getArguments().isEmpty()){
            EmbedBuilder embed = embedBuilder("Special Snowflakes");

            for (String discordUserId : snowflakes.keySet()) {
                StringBuilder sb = new StringBuilder();

                for (String discordServerId : snowflakes.get(discordUserId).keySet()) {

                    for (String discordChannelId : snowflakes.get(discordUserId).get(discordServerId).keySet()) {
                        String channelName = Objects.requireNonNull(Objects.requireNonNull(ctx.getJDA().getGuildById(discordServerId)).getTextChannelById(discordChannelId)).getAsMention();

                        for (String command : snowflakes.get(discordUserId).get(discordServerId).get(discordChannelId)) {
                            sb.append(channelName).append(" - ").append(command).append("\n");
                        }
                    }
                }

                User user = ctx.getJDA().retrieveUserById(discordUserId).complete();
                embed.addField(user.getAsTag(), sb.toString(), false);
            }

            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
            sendMessage(ctx, mca, 64);
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

                if (!PermissionManager.getWhitelistedServers().contains(discordServerId)) {
                    throw new IllegalArgumentException();
                }

                if (Objects.requireNonNull(ctx.getJDA().getGuildById(discordServerId)).getTextChannelById(discordChannelId) == null) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }

            JDA jda = ctx.getJDA();
            HashMap<String, HashMap<String, ArrayList<String>>> snowflakeUserPermissions;
            boolean remove = snowFlakeCheck(snowflakes, discordUserId, discordServerId, discordChannelId, command);

            if (snowflakes.containsKey(discordUserId)) {
                snowflakeUserPermissions = snowflakes.get(discordUserId);
            } else {
                snowflakeUserPermissions = new HashMap<>();
            }

            if (!remove) {
                snowflakeUserPermissions.putIfAbsent(discordServerId, new HashMap<>());
                snowflakeUserPermissions.get(discordServerId).putIfAbsent(discordChannelId, new ArrayList<>());
                snowflakeUserPermissions.get(discordServerId).get(discordChannelId).add(command);
            } else {
                snowflakeUserPermissions.get(discordServerId).get(discordChannelId).remove(command);

                if (snowflakeUserPermissions.get(discordServerId).get(discordChannelId).isEmpty()) {
                    snowflakeUserPermissions.get(discordServerId).remove(discordChannelId);

                    if (snowflakeUserPermissions.get(discordServerId).isEmpty()) {
                        snowflakeUserPermissions.remove(discordServerId);
                    }
                }
            }

            usersDAO.updateSnowflakePermissions(discordUserId, snowflakeUserPermissions);

            if (remove) {
                LOGGER.info("Removed snowflake permission for " + Objects.requireNonNull(jda.getUserById(discordUserId)).getName()
                        + " in " + Objects.requireNonNull(jda.getGuildById(discordServerId)).getName()
                        + " > " + Objects.requireNonNull(jda.getTextChannelById(discordChannelId)).getName() + " for " + command);
            } else {
                LOGGER.info("Added snowflake permission for " + Objects.requireNonNull(jda.getUserById(discordUserId)).getName()
                        + " in " + Objects.requireNonNull(jda.getGuildById(discordServerId)).getName()
                        + " > " + Objects.requireNonNull(jda.getTextChannelById(discordChannelId)).getName() + " for " + command);
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
        embed.addField("__Usage__", "```" + Config.PREFIX + getName() + " [<-s> <Server ID>] [<-c> <Channel ID>] <-u> <User> <-cmd> <Command>```", false);
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
        if (argumentPattern == null) {
            StringBuilder sb = new StringBuilder("^(?:(?:-s\\s\\d{18,19}\\s)?(?:-c\\s(?:<#)?\\d{18,19}>?\\s)?-u\\s(?:<@)?\\d{18,19}>?(?:\\s-cmd\\s(?:");

            for (CommandInterface ci : cm.getCommands()) {
                if (ci.getRestrictionLevel() < 3) {
                    sb.append(ci.getNameLC()).append("|");
                }
            }

            sb.deleteCharAt(sb.length() - 1);
            sb.append("))?)?\\s?$");

            argumentPattern = Pattern.compile(sb.toString());
        }

        return argumentPattern.matcher(args).matches();
    }
}
