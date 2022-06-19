package commandHandling;

import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import resources.CONFIG;

import java.util.List;

public class CommandContext implements ICommandContext {
    private final GuildMessageReceivedEvent event;
    private final List<String> arguments;
    private final int securityClearance;

    // ---------------------------------------------------------
    // SecurityClearance:
    // 0 - Owner
    // 1 - Administrator
    // 2 - Moderators
    // 3 - Plebs
    // ---------------------------------------------------------

    public CommandContext(GuildMessageReceivedEvent event, List<String> arguments) {
        this.event = event;
        this.arguments = arguments;

        securityClearance = event.getAuthor().getId().equals(CONFIG.OwnerID.get()) ? 0 :
        event.getMember().hasPermission(Permission.ADMINISTRATOR) ? 1 :
        event.getMember().hasPermission(Permission.KICK_MEMBERS) ? 2 : 3;
    }

    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return event;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public int getSecurityClearance() {
        return securityClearance;
    }
}
