package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import resources.EMOTES;
import services.BotExceptions;
import services.CommandManager;
import services.Miscellaneous;
import services.database.DatabaseHandler;

public class Channel implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Channel.class);
    private final CommandManager cm;

    public Channel(CommandManager cm, Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
        this.cm = cm;
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);

        String cmd, channel = ctx.getChannel().getId();

        try {
            if (cm.getCommand((cmd = ctx.getArguments().get(0))) != null) {
                if (cm.getCommand(cmd).isOwnerOnly()) {
                    return;
                } else if (channelCheck(cmd, channel)) {
                    DatabaseHandler.removeChannel(cmd, channel);
                } else {
                    DatabaseHandler.insertChannel(cmd, channel);
                }
            } else if (ctx.getArguments().get(0).equals("all")) {
                for (CommandInterface ci : cm.getCommands()) {
                    if (!ci.isOwnerOnly() && !channelCheck(ci.getName().toLowerCase(), channel)) {
                        DatabaseHandler.insertChannel(ci.getName().toLowerCase(), channel);
                    }
                }
            } else {
                BotExceptions.commandNotFoundException(ctx, ctx.getArguments().get(0));
                return;
            }
            CONFIG.reload();
        } catch (Exception e) {
            EmbedBuilder embed = Miscellaneous.embedBuilder("Whitelisted commands for this channel");
            StringBuilder sb = new StringBuilder();

            for (CommandInterface ci : cm.getCommands()) {
                if (ci.isOwnerOnly()) {
                    continue;
                } else if (channelCheck(ci.getName().toLowerCase(), channel)) {
                    sb.append(EMOTES.RDG.getAsEmote());
                } else {
                    sb.append(EMOTES.RDR.getAsEmote());
                }
                sb.append(ci.getName().toLowerCase()).append("\n");
            }
            embed.setDescription(sb.toString());

            ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                    msg -> Miscellaneous.deleteMsg(msg, 32)
            );
        }
    }

    private boolean channelCheck(String cmd, String channel) {
        return CONFIG.channels.get(cmd) != null && CONFIG.channels.get(cmd).contains(channel);
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
    public boolean isOwnerOnly() {
        return true;
    }
}
