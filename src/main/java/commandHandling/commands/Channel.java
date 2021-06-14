package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import resources.CONFIG;
import resources.EMOTES;
import services.BotExceptions;
import services.CommandManager;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Channel implements CommandInterface {
    private final CommandManager cm;

    public Channel(Logger LOGGER, CommandManager cm) {
        LOGGER.info("Loaded Command Channel");
        this.cm = cm;
    }

    @Override
    public void handle(CommandContext ctx) {
        String cmd, channel = ctx.getChannel().getId();
        services.Logger.command(ctx, "channel", true);

        try {
            if (cm.getCommand((cmd = ctx.getArguments().get(0))) != null) {
                if (cm.getCommand(cmd).isOwnerOnly()) {
                    return;
                } else if (CONFIG.commandChannelCheck(cmd, channel)) {
                    services.database.dbHandlerPermissions.removeFromChannels(cmd, channel);
                } else {
                    services.database.dbHandlerPermissions.addToChannels(cmd, channel);
                }
            } else if (ctx.getArguments().get(0).equals("all")) {
                for (CommandInterface ci : cm.getCommands()) {
                    if (!ci.isOwnerOnly() && !CONFIG.commandChannelCheck(ci.getName().toLowerCase(), channel)) {
                        services.database.dbHandlerPermissions.addToChannels(ci.getName().toLowerCase(), channel);
                    }
                }
            } else {
                BotExceptions.commandNotFoundException(ctx, ctx.getArguments().get(0));
                return;
            }
            CONFIG.reload();
        } catch (Exception e) {
            EmbedBuilder embed = new EmbedBuilder();
            StringBuilder sb = new StringBuilder();

            embed.setTitle("Whitelisted commands for this channel");
            embed.setColor(new Color(0xb074ad));

            for (CommandInterface ci : cm.getCommands()) {
                if (ci.isOwnerOnly()) {
                    continue;
                } else if (CONFIG.commandChannelCheck(ci.getName().toLowerCase(), channel)) {
                    sb.append(EMOTES.RDG.getAsEmote());
                } else {
                    sb.append(EMOTES.RDR.getAsEmote());
                }
                sb.append(ci.getName().toLowerCase()).append("\n");
            }
            embed.setDescription(sb.toString());

            ctx.getChannel().sendMessage(embed.build()).queue(
                    msg -> msg.delete().queueAfter(32, TimeUnit.SECONDS)
            );
        }
    }

    @Override
    public String getName() {
        return "Channel";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Channel");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Permit or deny the use of a command in this channel");
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
