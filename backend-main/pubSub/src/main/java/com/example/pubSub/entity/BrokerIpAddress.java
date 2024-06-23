package com.example.pubSub.entity;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;


@Document(collection = "brokerIpAddress")
public class BrokerIpAddress {
   
    private String id;

    private String ipAddress;

    @CreatedDate
    private Date createdAt;

    // Constructors, getters, and setters

    public BrokerIpAddress() {
        // Default constructor needed by JPA
    }

    public BrokerIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
   
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}