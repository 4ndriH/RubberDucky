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
        String cmd, channel = ctx.getChannel().getId();

        try {
            if (cm.getCommand((cmd = ctx.getArguments().get(0))) != null) {
                if (cm.getCommand(cmd).getRestrictionLevel() < 3) {
                    return;
                } else if (channelCheck(channels, cmd, channel)) {
                    DBHandlerWhitelistedChannels.removeChannelFromWhitelist(channel, cmd);
                } else {
                    DBHandlerWhitelistedChannels.addChannelToWhitelist(channel, cmd);
                }
            } else if (ctx.getArguments().get(0).equals("all")) {
                for (CommandInterface ci : cm.getCommands()) {
                    if (ci.getRestrictionLevel() == 3 && !channelCheck(channels, ci.getNameLC(), channel)) {
                        DBHandlerWhitelistedChannels.addChannelToWhitelist(channel, ci.getName().toLowerCase());
                    }
                }
            } else {
                BotExceptions.commandNotFoundException(ctx, ctx.getArguments().get(0));
                return;
            }
            PermissionManager.reload();
        } catch (Exception e) {
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
        }
    }

    private boolean channelCheck(HashMap<String, ArrayList<String>> channels, String discordChannelId, String command) {
        return channels.get(discordChannelId) != null && channels.get(discordChannelId).contains(command);
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
    public int getRestrictionLevel() {
        return 2;
    }
}
