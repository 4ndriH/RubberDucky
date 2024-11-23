package commandhandling;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import assets.Config;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandContext {
    private final MessageReceivedEvent event;
    private final ArrayList<String> arguments;
    private final int securityClearance;
    public boolean persist;

    // ---------------------------------------------------------
    // SecurityClearance:
    // 0 - Owner
    // 1 - Administrator
    // 2 - Moderators
    // 3 - Plebs
    // ---------------------------------------------------------

    public CommandContext(MessageReceivedEvent event, ArrayList<String> arguments) {
        this.event = event;
        this.arguments = arguments;

        persist = this.arguments.remove("-p");

        securityClearance = event.getAuthor().getId().equals(Config.OWNER_ID) ? 0 :
        Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR) ? 1 :
        event.getMember().hasPermission(Permission.KICK_MEMBERS) ? 2 : 3;
    }

    public MessageReceivedEvent getEvent() {
        return this.event;
    }

    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    public List<String> getArguments() {
        return arguments;
    }

    public net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion getChannel() {
        return this.event.getChannel();
    }

    public net.dv8tion.jda.api.entities.Message getMessage() {
        return this.event.getMessage();
    }

    public net.dv8tion.jda.api.entities.User getAuthor() {
        return this.event.getAuthor();
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

    // allow only moderators or higher to set a message as persistent
    public boolean isPersistent() {
        return persist && securityClearance < 3;
    }
}
