package com.example.pubSub.service;

import com.example.pubSub.entity.Publisher;
import com.example.pubSub.repository.PublishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublishService {

    @Autowired
    private PublishRepository publishRepository;

    public Publisher findByUsername(String username) {
        return publishRepository.findByUsername(username);
    }

    public void savePublisher(Publisher publisher) {
        publishRepository.save(publisher);
    }

    public boolean checkCredentials(String username, String password) {
        Publisher publisher = publishRepository.findByUsername(username);
        return publisher != null && publisher.getPassword().equals(password);
    }
}
