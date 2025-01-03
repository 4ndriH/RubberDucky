package commandhandling.commands.mod;

import assets.Emotes;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.CommandManager;
import services.PermissionManager;
import services.database.daos.AccessControlDAO;
import services.discordhelpers.EmbedHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class Channel implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(Channel.class);
    public static Pattern argumentPattern = null;
    private final CommandManager cm;

    public Channel(CommandManager cm) {
        this.cm = cm;
    }

    @Override
    public void handle(CommandContext ctx) {
        AccessControlDAO accessControlDAO = new AccessControlDAO();
        HashMap<String, ArrayList<String>> channels = accessControlDAO.getChannelIds();
        String channel = ctx.getChannel().getId();
        String server = ctx.getGuild().getId();

        if (ctx.getArguments().isEmpty()) {
            EmbedBuilder embed = EmbedHelper.embedBuilder("Whitelisted commands for this channel");
            StringBuilder sb = new StringBuilder();

            for (CommandInterface ci : cm.getCommands()) {
                if (ci.getRestrictionLevel() < 3) {
                    continue;
                } else if (channelCheck(channels, ci.getNameLC(), channel)) {
                    sb.append(Emotes.RDG.getAsEmote());
                } else {
                    sb.append(Emotes.RDR.getAsEmote());
                }
                sb.append(ci.getNameLC()).append("\n");
            }
            embed.setDescription(sb.toString());

            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
            sendMessage(ctx, mca, 32);
        } else {
            String cmd = ctx.getArguments().get(0);

            if (cmd.equals("allOn")) {
                for (CommandInterface ci : cm.getCommands()) {
                    if (ci.getRestrictionLevel() == 3 && !channelCheck(channels, ci.getNameLC(), channel)) {
                        accessControlDAO.addCommand(server, channel, ci.getNameLC());
                    }
                }
                LOGGER.info("All commands enabled for channel {}", ctx.getChannel().getName());
            } else if (cmd.startsWith("allOff")) {
                for (CommandInterface ci : cm.getCommands()) {
                    if (ci.getRestrictionLevel() == 3 && channelCheck(channels, ci.getNameLC(), channel)) {
                        accessControlDAO.removeCommand(server, channel, ci.getNameLC());
                    }
                }
                LOGGER.info("All commands disabled for channel {}", ctx.getChannel().getName());
            } else {
                if (channelCheck(channels, cmd, channel)) {
                    accessControlDAO.removeCommand(server, channel, cmd);
                    LOGGER.info("Command `{}` disabled for channel {}", cmd, ctx.getChannel().getName());
                } else {
                    accessControlDAO.addCommand(server, channel, cmd);
                    LOGGER.info("Command `{}` enabled for channel {}", cmd, ctx.getChannel().getName());
                }
            }
            PermissionManager.reload();
        }
    }

    private boolean channelCheck(HashMap<String, ArrayList<String>> channels, String command, String channelId) {
        return channels.get(channelId) != null && channels.get(channelId).contains(command);
    }

    @Override
    public String getName() {
        return "Channel";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Permit or deny the use of a command in this channel");
        return embed;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        if (argumentPattern == null) {
            StringBuilder sb = new StringBuilder();

            for (CommandInterface ci : cm.getCommands()) {
                if (ci.getRestrictionLevel() == 3) {
                    sb.append(ci.getNameLC()).append("|");

                    for (String alias : ci.getAliases()) {
                        sb.append(alias).append("|");
                    }
                }
            }

            argumentPattern = Pattern.compile("^((?:" + sb + "allOn|allOff)?)\\s?$");
        }

        return argumentPattern.matcher(args).matches();
    }
}
