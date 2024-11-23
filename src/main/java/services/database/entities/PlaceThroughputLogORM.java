package services.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "place_throughput_log")
public class PlaceThroughputLogORM {
    @Id
    @Column(name = "time_stamp")
    private LocalDateTime timestamp;

    @Column(name = "batch_size")
    private int batchSize;

    @Column(name = "message_batch_time")
    private int messageBatchTime;

    public PlaceThroughputLogORM() {}

    public PlaceThroughputLogORM(LocalDateTime timestamp, int numberOfPixels, int messageBatchTime) {
        this.timestamp = timestamp;
        this.batchSize = numberOfPixels;
        this.messageBatchTime = messageBatchTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int numberOfPixels) {
        this.batchSize = numberOfPixels;
    }

    public int getMessageBatchTime() {
        return messageBatchTime;
    }

    public void setMessageBatchTime(int messageBatchTime) {
        this.messageBatchTime = messageBatchTime;
    }
}
