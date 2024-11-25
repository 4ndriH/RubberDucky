package services.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "place_throughput_log")
public class PlaceThroughputLogORM {
    @Id
    @Column(name = "key")
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
