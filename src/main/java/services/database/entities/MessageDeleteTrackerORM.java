package services.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_delete_tracker")
public class MessageDeleteTrackerORM {
    @Id
    @Column(name = "discord_message_id")
    private String discordMessageId;

    @Column(name = "discord_server_id")
    private String discordServerId;

    @Column(name = "discord_channel_id")
    private String discordChannelId;

    @Column(name = "time_to_delete")
    private LocalDateTime timeToDelete;

    public MessageDeleteTrackerORM() {}

    public MessageDeleteTrackerORM(String discordMessageId, String discordServerId, String discordChannelId, LocalDateTime timeToDelete) {
        this.discordMessageId = discordMessageId;
        this.discordServerId = discordServerId;
        this.discordChannelId = discordChannelId;
        this.timeToDelete = timeToDelete;
    }

    public String getDiscordMessageId() {
        return discordMessageId;
    }

    public void setDiscordMessageId(String discordMessageId) {
        this.discordMessageId = discordMessageId;
    }

    public String getDiscordServerId() {
        return discordServerId;
    }

    public void setDiscordServerId(String discordServerId) {
        this.discordServerId = discordServerId;
    }

    public String getDiscordChannelId() {
        return discordChannelId;
    }

    public void setDiscordChannelId(String discordChannelId) {
        this.discordChannelId = discordChannelId;
    }

    public LocalDateTime getTimeToDelete() {
        return timeToDelete;
    }

    public void setTimeToDelete(LocalDateTime timeToDelete) {
        this.timeToDelete = timeToDelete;
    }
}
