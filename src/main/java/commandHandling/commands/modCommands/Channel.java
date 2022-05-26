package commandHandling.commands.modCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.EMOTES;
import services.BotExceptions;
import services.CommandManager;
import services.PermissionManager;
import services.database.DBHandlerWhitelistedChannels;
import services.logging.EmbedHelper;

public class Channel implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Channel.class);
    private final CommandManager cm;

    public Channel(CommandManager cm, Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
        this.cm = cm;
    }

    @Override
    public void handle(CommandContext ctx) {
        String cmd, channel = ctx.getChannel().getId();

        try {
            if (cm.getCommand((cmd = ctx.getArguments().get(0))) != null) {
                if (cm.getCommand(cmd).getRestrictionLevel() < 3) {
                    return;
                } else if (channelCheck(cmd, channel)) {
                    DBHandlerWhitelistedChannels.removeChannelFromWhitelist(cmd, channel);
                } else {
                    DBHandlerWhitelistedChannels.addChannelToWhitelist(cmd, channel);
                }
            } else if (ctx.getArguments().get(0).equals("all")) {
                for (CommandInterface ci : cm.getCommands()) {
                    if (ci.getRestrictionLevel() == 3 && !channelCheck(ci.getName().toLowerCase(), channel)) {
                        DBHandlerWhitelistedChannels.addChannelToWhitelist(ci.getName().toLowerCase(), channel);
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
                } else if (channelCheck(ci.getName().toLowerCase(), channel)) {
                    sb.append(EMOTES.RDG.getAsEmote());
                } else {
                    sb.append(EMOTES.RDR.getAsEmote());
                }
                sb.append(ci.getName().toLowerCase()).append("\n");
            }
            embed.setDescription(sb.toString());

            EmbedHelper.sendEmbed(ctx, embed, 32);
        }
    }

    private boolean channelCheck(String cmd, String channel) {
        return PermissionManager.channels.get(cmd) != null && PermissionManager.channels.get(cmd).contains(channel);
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
