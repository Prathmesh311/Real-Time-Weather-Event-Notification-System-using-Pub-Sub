package com.example.pubSub.service;

import com.example.pubSub.entity.Manager;
import com.example.pubSub.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    public void saveSectors(List<String> sectors) {
        for (String sector : sectors) {
            Manager manager = new Manager(sector);
            managerRepository.save(manager);
        }
    }

    public List<Manager> fetchEnabledManagers() {
        return managerRepository.findAllByIsEnabledTrue();
    }
}
