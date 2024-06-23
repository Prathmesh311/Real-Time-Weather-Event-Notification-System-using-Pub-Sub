package com.example.pubSub.model;

import java.util.List;

public class SectorListRequest {
    private List<String> sectors;
    private List<String> sectorIds;

    public List<String> getSectorIds() {
        return sectorIds;
    }

    public void setSectorIds(List<String> sectorIds) {
        this.sectorIds = sectorIds;
    }

    public List<String> getSectors() {
        return sectors;
    }

    public void setSectors(List<String> sectors) {
        this.sectors = sectors;
    }

    @Override
    public String toString() {
        return "SectorListRequest{" +
                "sectorIds=" + sectorIds +
                ", sectors=" + sectors +
                '}';
    }
}
