package commandhandling.commands.mod;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Emotes;
import services.BotExceptions;
import services.CommandManager;
import services.PermissionManager;
import services.database.DBHandlerWhitelistedChannels;
import services.discordhelpers.EmbedHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import static services.PermissionManager.getWhitelistedChannels;

public class Channel implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Channel.class);
    private final CommandManager cm;

    public Channel(CommandManager cm) {
        this.cm = cm;
    }

    @Override
    public void handle(CommandContext ctx) {
        HashMap<String, ArrayList<String>> channels = getWhitelistedChannels();
        String channel = ctx.getChannel().getId();

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

            EmbedHelper.sendEmbed(ctx, embed, 32);
        } else {
            String cmd = ctx.getArguments().get(0);

            if (cmd.equals("allOn")) {
                for (CommandInterface ci : cm.getCommands()) {
                    if (ci.getRestrictionLevel() == 3 && !channelCheck(channels, ci.getNameLC(), channel)) {
                        DBHandlerWhitelistedChannels.addChannelToWhitelist(channel, ci.getNameLC());
                    }
                }
            } else if (ctx.getArguments().get(0).startsWith("allOff")) {
                for (CommandInterface ci : cm.getCommands()) {
                    if (ci.getRestrictionLevel() == 3 && channelCheck(channels, ci.getNameLC(), channel)) {
                        DBHandlerWhitelistedChannels.removeChannelFromWhitelist(channel, ci.getNameLC());
                    }
                }
            } else {
                if (channelCheck(channels, cmd, channel)) {
                    DBHandlerWhitelistedChannels.removeChannelFromWhitelist(channel, cmd);
                } else {
                    DBHandlerWhitelistedChannels.addChannelToWhitelist(channel, cmd);
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
        StringBuilder sb = new StringBuilder();

        for (CommandInterface ci : cm.getCommands()) {
            if (ci.getRestrictionLevel() == 3) {
                sb.append(ci.getNameLC()).append("|");
            }
        }

        Pattern argumentPattern = Pattern.compile("^((?:" + sb + "|allOn|allOff)?)\\s?$");
        return argumentPattern.matcher(args).matches();
    }
}
