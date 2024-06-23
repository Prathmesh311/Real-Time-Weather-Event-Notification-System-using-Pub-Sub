package com.example.pubSub.entity;

public class PublishSectorOffset {

    private String publishSector;
    private int offSet;

    // Constructor
    public PublishSectorOffset(String publishSector, int offSet) {
        this.publishSector = publishSector;
        this.offSet = offSet;
    }

    // Getters
    public String getPublishSector() {
        return publishSector;
    }

    public int getOffset() {
        return offSet;
    }
    
}
