package com.example.pubSub.controller;

import com.example.pubSub.entity.BrokerIpAddress;
import com.example.pubSub.entity.SubscriberMapped;
import com.example.pubSub.entity.Manager;
import com.example.pubSub.entity.Message;
import com.example.pubSub.entity.Publisher;
import com.example.pubSub.entity.SubscriberUpdateRequest;
import com.example.pubSub.model.BrokerIp;
import com.example.pubSub.model.SectorListRequest;
import com.example.pubSub.model.ResponseMessage;
import com.example.pubSub.repository.BrokerIpAddressRepository;
import com.example.pubSub.service.IpStorageService;
import com.example.pubSub.service.ManagerService;
import com.example.pubSub.service.MessageService;
import com.example.pubSub.service.PublishService;
import com.example.pubSub.service.SubscribeMessageService;
import org.springframework.http.*;

import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/publishers")
public class PublishingController {

    private final IpStorageService ipStorageService;

    @Autowired
    private PublishService publishService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private SubscribeMessageService subscribeMessageService;
    @Autowired
    private BrokerIpAddressRepository brokerIpAddressRepository;

    public PublishingController(IpStorageService ipStorageService) {
        this.ipStorageService = ipStorageService;
    }

    // Register a new publisher
    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> createPublisher(@RequestBody Publisher publisher) {
        String username = publisher.getUsername();
        String password = publisher.getPassword();
    
        // Check if the user already exists
        if (publishService.findByUsername(username) != null) {
            return new ResponseEntity<>(
                new ResponseMessage(HttpStatus.CONFLICT.value(), "Username already exists"),
                HttpStatus.CONFLICT
            );
        }
    
        // Save the new publisher
        Publisher newPublisher = new Publisher(username, password);
        publishService.savePublisher(newPublisher);
    
        return new ResponseEntity<>(
            new ResponseMessage(HttpStatus.CREATED.value(), "Publisher registered successfully"),
            HttpStatus.CREATED
        );
    }
    
    // Validate publisher credentials
    @PostMapping("/validate")
    public ResponseEntity<ResponseMessage> verifyPublisher(@RequestBody Publisher publisher) {
        String username = publisher.getUsername();
        String password = publisher.getPassword();
    
        if (publishService.checkCredentials(username, password)) {
            return new ResponseEntity<>(
                new ResponseMessage(HttpStatus.OK.value(), "Valid credentials"),
                HttpStatus.OK
            );
        } else {
            Publisher existingPublisher = publishService.findByUsername(username);
            if (existingPublisher != null) {
                return new ResponseEntity<>(
                    new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Incorrect password"),
                    HttpStatus.UNAUTHORIZED
                );
            } else {
                return new ResponseEntity<>(
                    new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Publisher not found"),
                    HttpStatus.NOT_FOUND
                );
            }
        }
    }

    // Insert sector data
    @PostMapping("/sectors")
    public ResponseEntity<ResponseMessage> insertSectors(@RequestBody SectorListRequest sectorListRequest) {
        List<String> sectors = sectorListRequest.getSectors();
        managerService.saveSectors(sectors);
        return new ResponseEntity<>(
            new ResponseMessage(HttpStatus.CREATED.value(), "Sectors saved successfully"),
            HttpStatus.CREATED
        );
    }

    // Fetch all enabled managers
    @GetMapping("/managers")
    public ResponseEntity<List<Manager>> getAllEnabledManagers() {
        List<Manager> managers = managerService.fetchEnabledManagers();
        return ResponseEntity.ok(managers);
    }

    // Publish a new message
    @PostMapping("/publish")
    public ResponseEntity<ResponseMessage> publishMessage(@RequestBody Message message) {
        try {
            // Save the message
            Message savedMessage = messageService.saveMessage(message);
            
            // Log the message for subscribers
            subscribeMessageService.logSubscriberMessage(savedMessage);
            String brokerResponse = messageService.sendToBroker(savedMessage);
    
            return new ResponseEntity<>(
                new ResponseMessage(HttpStatus.CREATED.value(), brokerResponse),
                HttpStatus.CREATED
            );
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                new ResponseMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage()),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Fetch and store subscriber data mappings
    @PostMapping("/updateSubscriberData")
    public ResponseEntity<String> updateSubscriberData(@RequestBody SubscriberUpdateRequest updateRequest) {
        try {
            String response = messageService.processSubscriberData(updateRequest);
    
            if (response.contains("Failed")) {
                return new ResponseEntity<>("Error: " + response, HttpStatus.BAD_GATEWAY);
            } else {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Register broker IP address
    @PostMapping("/registerBrokerIp")
    public ResponseEntity<String> registerBrokerIp(@RequestBody BrokerIp brokerIp) {
        try {
            String ipAddress = brokerIp.getIpAddress();
            System.out.println("Broker IP address registered: " + ipAddress);

            ipStorageService.storeGlobalIpAddress(ipAddress);

            // Save Broker IP Address entity
            BrokerIpAddress brokerIpAddress = new BrokerIpAddress(ipAddress);
            brokerIpAddress.setCreatedAt(new Date());
            brokerIpAddressRepository.save(brokerIpAddress);

            return new ResponseEntity<>("IP address saved", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
