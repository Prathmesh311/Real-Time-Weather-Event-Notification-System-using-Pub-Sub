package com.example.pubSub.repository;


import com.example.pubSub.entity.Manager;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerRepository extends MongoRepository<Manager, String> {
	boolean existsById(String id);
   
	List<Manager> findAllByIsEnabledTrue();
}
