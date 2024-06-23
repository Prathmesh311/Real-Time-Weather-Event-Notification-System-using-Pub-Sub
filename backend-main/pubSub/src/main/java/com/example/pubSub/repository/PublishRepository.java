package com.example.pubSub.repository;

import com.example.pubSub.entity.Publisher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishRepository extends MongoRepository<Publisher, String> {
    Publisher findByUsername(String username);
}
