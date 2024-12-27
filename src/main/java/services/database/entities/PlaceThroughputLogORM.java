package services.database.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "place_throughput_log")
public class PlaceThroughputLogORM {
    @Id
    @Column(name = "key")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "place_throughput_log_key_seq")
    @SequenceGenerator(name = "place_throughput_log_key_seq", sequenceName = "place_throughput_log_key_seq", allocationSize = 1)
    private int key;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "batch_size")
    private int batchSize;

    @Column(name = "message_batch_time")
    private int messageBatchTime;

    public PlaceThroughputLogORM() {}

    public PlaceThroughputLogORM(int key, long createdAt, int numberOfPixels, int messageBatchTime) {
        this.key = key;
        this.createdAt = createdAt;
        this.batchSize = numberOfPixels;
        this.messageBatchTime = messageBatchTime;
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
