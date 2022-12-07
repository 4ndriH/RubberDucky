package assets.Objects;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.jetbrains.annotations.NotNull;

public class DeletableMessage implements Comparable<DeletableMessage> {
    String DiscordServerID;
    String DiscordChannelID;
    String DiscordMessageID;
    long deletionTime;

    public DeletableMessage(String DiscordServerID, String DiscordChannelID, String DiscordMessageID, long deletionTime) {
        this.DiscordServerID = DiscordServerID;
        this.DiscordChannelID = DiscordChannelID;
        this.DiscordMessageID = DiscordMessageID;
        this.deletionTime = deletionTime;
    }

    public Message getMessage(JDA jda) {
        try {
            return jda.getGuildById(DiscordServerID).getTextChannelById(DiscordChannelID).retrieveMessageById(DiscordMessageID).complete();
        } catch (Exception e) {
            try {
                return jda.getGuildById(DiscordServerID).getThreadChannelById(DiscordChannelID).retrieveMessageById(DiscordMessageID).complete();
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public boolean deleteLater(long currentSystemTime) {
        return deletionTime - currentSystemTime > 0;
    }

    @Override
    public int compareTo(@NotNull DeletableMessage o) {
        int DiscordServerIdCompare = DiscordServerID.compareTo(o.DiscordServerID);
        return DiscordServerIdCompare == 0 ? DiscordChannelID.compareTo(o.DiscordChannelID) : DiscordServerIdCompare;
    }
}
