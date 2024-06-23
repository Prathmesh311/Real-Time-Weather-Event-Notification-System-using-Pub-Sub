package com.example.pubSub.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pubSub.entity.SubscriberActivityLogs;

import java.lang.Long;


public interface SubscribeMessageRepository extends MongoRepository<SubscriberActivityLogs, String> {
    List<SubscriberActivityLogs> findByPublishManagerIdIn(List<String> publishMasterIds);
    List<SubscriberActivityLogs> findByPublishManagerIdInAndSubscriberNameAndIsEnable(
            List<String> publishManagerIds, String subscriberName, boolean isEnable);
            

     // Define a method to find the maximum offset for a given publishMasterId
    @Query("SELECT s FROM SubscriberActivityLog s WHERE s.publishManagerId = :publishManagerId ORDER BY s.offset DESC LIMIT 1")
    Long findMaxOffsetByPublishManagerId(@Param("publishManagerId") String publishMasterId);

}