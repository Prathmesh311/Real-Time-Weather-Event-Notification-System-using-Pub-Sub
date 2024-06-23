package com.example.pubSub.entity;

import java.util.List;

public class SubscriberUpdateRequest {
    private String username;
    private List<String> sectorIds;

    public SubscriberUpdateRequest() {
    }

    public SubscriberUpdateRequest(String username, List<String> sectorIds) {
        this.username = username;
        this.sectorIds = sectorIds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getSectorIds() {
        return sectorIds;
    }

    public void setSectorIds(List<String> sectorIds) {
        this.sectorIds = sectorIds;
    }
}
