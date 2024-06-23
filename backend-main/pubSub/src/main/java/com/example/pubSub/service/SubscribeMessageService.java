package com.example.pubSub.service;

import com.example.pubSub.entity.Message;
import com.example.pubSub.entity.SubscriberActivityLogs;
import com.example.pubSub.repository.SubscribeMessageRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class SubscribeMessageService {

    @Autowired
    private SubscribeMessageRepository subscribeMessageRepository;

    @Transactional
    public void logSubscriberMessage(Message message) {
        
        SubscriberActivityLogs subscriberActivityLogs = new SubscriberActivityLogs();
        subscriberActivityLogs.setPublishManagerId(message.getManagerId());
        subscriberActivityLogs.setQueue(message.getQueue());
        subscriberActivityLogs.setPublishedDataId(message.getId());
        subscriberActivityLogs.setMessage(message.getMessage());
        subscriberActivityLogs.setSubscriberName("null");


        // save subscriber activity logs
        subscribeMessageRepository.save(subscriberActivityLogs);
    }
}
