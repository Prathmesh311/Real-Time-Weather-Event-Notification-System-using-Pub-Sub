package com.example.pubSub.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "subscriberMapped")
public class SubscriberMapped {

    @Id
    private String id;
    private String managerId;
    private String queue;
    private String username;
    private int offset;
    private LocalDateTime timestamp;
    private String sector;

    public SubscriberMapped() {
    }

    public SubscriberMapped(String managerId, String queue, String username) {
        this.managerId = managerId;
        this.queue = queue;
        this.username = username;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    @Override
    public String toString() {
        return "SubscriberMapped{" +
                "id='" + id + '\'' +
                ", managerId='" + managerId + '\'' +
                ", queue='" + queue + '\'' +
                ", username='" + username + '\'' +
                ", offset=" + offset +
                ", timestamp=" + timestamp +
                ", sector='" + sector + '\'' +
                '}';
    }
}
