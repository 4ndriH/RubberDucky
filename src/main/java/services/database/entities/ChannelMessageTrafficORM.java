package services.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "channel_message_traffic")
public class ChannelMessageTrafficORM {
    @Id
    @Column(name = "time_stamp")
    private LocalDateTime timestamp;

    @Column(name = "eth_place_bots")
    private int ethPlaceBots;

    @Column(name = "count_thread")
    private int countThread;

    public ChannelMessageTrafficORM() {}

    public ChannelMessageTrafficORM(LocalDateTime timestamp, int ethPlaceBots, int countThread) {
        this.timestamp = timestamp;
        this.ethPlaceBots = ethPlaceBots;
        this.countThread = countThread;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
