package assets.objects;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
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

    public boolean futureDeletion() {
        return deletionTime - System.currentTimeMillis() > 0;
    }

    public int deleteTime() {
        return (int)(deletionTime - System.currentTimeMillis()) / 1000;
    }

    @Override
    public int compareTo(@NotNull DeletableMessage o) {
        int DiscordServerIdCompare = DiscordServerID.compareTo(o.DiscordServerID);
        return DiscordServerIdCompare == 0 ? DiscordChannelID.compareTo(o.DiscordChannelID) : DiscordServerIdCompare;
    }
}
