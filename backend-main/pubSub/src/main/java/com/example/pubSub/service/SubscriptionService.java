package com.example.pubSub.service;

import com.example.pubSub.entity.UserSubscriber;
import com.example.pubSub.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public UserSubscriber findUserByUsername(String username) {
        return subscriptionRepository.findByUsername(username);
    }

    public void saveUser(UserSubscriber user) {
        subscriptionRepository.save(user);
    }

    public boolean areCredentialsValid(String username, String password) {
        UserSubscriber user = subscriptionRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
}
