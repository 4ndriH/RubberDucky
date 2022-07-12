package commandHandling;

import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import assets.CONFIG;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandContext { //implements ICommandContext {
    private final MessageReceivedEvent event;
    private final List<String> arguments;
    private final int securityClearance;

    // ---------------------------------------------------------
    // SecurityClearance:
    // 0 - Owner
    // 1 - Administrator
    // 2 - Moderators
    // 3 - Plebs
    // ---------------------------------------------------------

    public CommandContext(MessageReceivedEvent event, List<String> arguments) {
        this.event = event;
        this.arguments = arguments;

        securityClearance = event.getAuthor().getId().equals(CONFIG.OwnerID.get()) ? 0 :
        event.getMember().hasPermission(Permission.ADMINISTRATOR) ? 1 :
        event.getMember().hasPermission(Permission.KICK_MEMBERS) ? 2 : 3;
    }

    public MessageReceivedEvent getEvent() {
        return this.event;
    }

//    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    public List<String> getArguments() {
        return arguments;
    }

    public net.dv8tion.jda.api.entities.TextChannel getChannel() {
        return this.event.getTextChannel();
    }

    public net.dv8tion.jda.api.entities.Message getMessage() {
        return this.event.getMessage();
    }

    public net.dv8tion.jda.api.entities.User getAuthor() {
        return this.event.getAuthor();
    }

    public net.dv8tion.jda.api.entities.Member getMember() {
        return this.event.getMember();
    }

    public net.dv8tion.jda.api.JDA getJDA() {
        return this.event.getJDA();
    }

    public net.dv8tion.jda.api.entities.User getSelfUser() {
        return this.event.getJDA().getSelfUser();
    }


    public net.dv8tion.jda.api.entities.Member getSelfMember() {
        return this.event.getGuild().getMember(this.getSelfUser());
    }

    public int getSecurityClearance() {
        return securityClearance;
    }
}
