package com.example.pubSub.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.pubSub.entity.SubscriberMapped;
import com.example.pubSub.entity.Message;
@Repository
public interface SubscriberMappedRepository extends MongoRepository<SubscriberMapped, String> {

    // Find the last record for a given publishMasterId, ordered by the document's default order
    SubscriberMapped findFirstByPublishMasterIdOrderByTimestampDesc(String publishMasterId);

    SubscriberMapped findFirstByUsernameAndManagerIdOrderByTimestampDesc(String username, String publishMasterId);
    
}

