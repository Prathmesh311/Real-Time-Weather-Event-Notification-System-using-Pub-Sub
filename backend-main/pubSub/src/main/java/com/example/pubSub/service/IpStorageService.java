package com.example.pubSub.service;

import org.springframework.stereotype.Service;

@Service
public class IpStorageService {

    private String globalIpAddress;

    public void storeGlobalIpAddress(String ipAddress) {
        this.globalIpAddress = ipAddress;
    }

    public String getGlobalIpAddress() {
        return globalIpAddress;
    }
}
