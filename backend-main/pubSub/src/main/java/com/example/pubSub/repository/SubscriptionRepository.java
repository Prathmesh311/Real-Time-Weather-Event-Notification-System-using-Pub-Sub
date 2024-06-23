package com.example.pubSub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.pubSub.entity.UserSubscriber;

@Repository
public interface SubscriptionRepository extends MongoRepository<UserSubscriber, String> {
    UserSubscriber findByUsername(String username);
}
