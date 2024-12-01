package services.database.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "channel_message_traffic")
public class ChannelMessageTrafficORM {
    @Id
    @Column(name = "key")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channel_message_traffic_key_seq")
    @SequenceGenerator(name = "channel_message_traffic_key_seq", sequenceName = "channel_message_traffic_key_seq", allocationSize = 1)
    private int key;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "eth_place_bots")
    private int ethPlaceBots;

    @Column(name = "count_thread")
    private int countThread;

    public ChannelMessageTrafficORM() {}

    public ChannelMessageTrafficORM(int key, long createdAt, int ethPlaceBots, int countThread) {
        this.key = key;
        this.createdAt = createdAt;
        this.ethPlaceBots = ethPlaceBots;
        this.countThread = countThread;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getEthPlaceBots() {
        return ethPlaceBots;
    }

    public void setEthPlaceBots(int ethPlaceBots) {
        this.ethPlaceBots = ethPlaceBots;
    }

    public int getCountThread() {
        return countThread;
    }

    public void setCountThread(int countThread) {
        this.countThread = countThread;
    }
}
