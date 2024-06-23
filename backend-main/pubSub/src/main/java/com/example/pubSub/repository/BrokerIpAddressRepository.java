package com.example.pubSub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.pubSub.entity.BrokerIpAddress;

public interface BrokerIpAddressRepository extends MongoRepository<BrokerIpAddress, String> {
}