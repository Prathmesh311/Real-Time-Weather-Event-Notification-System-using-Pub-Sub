package com.example.pubSub.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.pubSub.entity.SubscriberMapped;
import com.example.pubSub.entity.Message;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByManagerIdIn(List<String> masterIds);

    @Query("{'masterId': { $in: ?1 }, ?0: false}")
    List<Message> findByUsernameAndFetchedForBrokerFalse(String columnName, List<String> publishMasterIds);


}
