package commandhandling;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import assets.Config;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandContext {
    private final ArrayList<String> arguments;
    private final ArrayList<Message.Attachment> attachments;
    private final int securityClearance;
    private final boolean persist;
    private final Guild guild;
    private final MessageChannelUnion channel;
    private final Message message;
    private final User author;
    private final JDA jda;
    private final User selfUser;
    private final Member selfMember;
    private final String commandContent;
    private final boolean slashCommand;

    // ---------------------------------------------------------
    // SecurityClearance:
    // 0 - Owner
    // 1 - Administrator
    // 2 - Moderators
    // 3 - Plebs
    // ---------------------------------------------------------

    public CommandContext(MessageReceivedEvent event, ArrayList<String> arguments, List<Message.Attachment> attachments) {
        this.arguments = arguments;
        this.guild = event.getGuild();
        this.channel = event.getChannel();
        this.message = event.getMessage();
        this.author = event.getAuthor();
        this.jda = event.getJDA();
        this.selfUser = event.getJDA().getSelfUser();
        this.selfMember = event.getGuild().getMember(this.selfUser);
        this.commandContent = event.getMessage().getContentRaw();
        this.attachments = new ArrayList<>(attachments);

        this.persist = this.arguments.remove("-p");
        this.slashCommand = false;

        securityClearance = event.getAuthor().getId().equals(Config.ownerID) ? 0 :
        Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR) ? 1 :
        event.getMember().hasPermission(Permission.KICK_MEMBERS) ? 2 : 3;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public ArrayList<Message.Attachment> getAttachments() {
        return attachments;
    }

    public MessageChannelUnion getChannel() {
        return this.channel;
    }

    public Message getMessage() {
        return this.message;
    }

    public String getCommandContent() {
        return this.commandContent;
    }

    public User getAuthor() {
        return this.author;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public User getSelfUser() {
        return this.selfUser;
    }

    public Member getSelfMember() {
        return this.selfMember;
    }

    public int getSecurityClearance() {
        return securityClearance;
    }

    // allow only moderators or higher to set a message as persistent
    public boolean isPersistent() {
        return persist && securityClearance < 3;
    }
}
