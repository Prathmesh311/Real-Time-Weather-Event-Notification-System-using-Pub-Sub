package com.example.pubSub.service;

import com.example.pubSub.entity.SubscriberMapped;
import com.example.pubSub.entity.Message;
import com.example.pubSub.entity.PublishSectorOffset;
import com.example.pubSub.entity.SubscriberUpdateRequest;
import com.example.pubSub.model.PublishDataApiBrokerRequest;
import com.example.pubSub.repository.SubscriberMappedRepository;
import com.example.pubSub.repository.ManagerRepository;
import com.example.pubSub.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class MessageService {

    private final IpStorageService ipStorageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SubscriberMappedRepository subscriberMappedRepository;

    @Autowired
    private ManagerRepository managerRepository;

    public MessageService(IpStorageService ipStorageService) {
        this.ipStorageService = ipStorageService;
    }

    private String retrieveGlobalIpAddress() {
        return ipStorageService.getGlobalIpAddress();
    }

    // Save a new message and link it to the manager
    public Message saveMessage(Message message) {
        if (managerRepository.existsById(message.getManagerId())) {
            return messageRepository.save(message);
        } else {
            throw new RuntimeException("Manager ID not found");
        }
    }

    // Retrieve messages by manager IDs
    public List<Message> getMessagesByManagerIds(List<String> managerIds) {
        return messageRepository.findByManagerIdIn(managerIds);
    }

    // Dispatch messages to the broker via HTTP call
    public String sendToBroker(Message message) {
        try {
            String queueName = message.getQueue();
            String messageContent = message.getMessage();

            PublishDataApiBrokerRequest brokerRequest = new PublishDataApiBrokerRequest(queueName, messageContent);

            String brokerUrl = "http://" + retrieveGlobalIpAddress() + "/publishData";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PublishDataApiBrokerRequest> requestEntity = new HttpEntity<>(brokerRequest, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(brokerUrl, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return "Broker API call successful. Response: " + response.getBody();
            } else {
                return "Broker API call failed. Status code: " + response.getStatusCode();
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String processSubscriberData(SubscriberUpdateRequest updateRequest) {
        String email = updateRequest.getUsername();
        String username = extractUsername(email);

        List<Message> messagesToUpdate = messageRepository.findByUsernameAndFetchedForBrokerFalse(username, updateRequest.getSectorIds());

        List<SubscriberMapped> mappedDataList = new ArrayList<>();
        for (Message message : messagesToUpdate) {
            int latestOffset = getLatestOffset(username, message.getManagerId());

            SubscriberMapped mappedData = new SubscriberMapped(message.getManagerId(), message.getQueue(), username);
            mappedData.setOffset(latestOffset + 1);
            mappedData.setTimestamp(LocalDateTime.now());

            SubscriberMapped savedMappedData = subscriberMappedRepository.save(mappedData);
            mappedDataList.add(savedMappedData);
        }

        List<PublishSectorOffset> latestOffsets = getLatestOffsets(username, mappedDataList);
        String brokerResponse = sendOffsetsToBroker(latestOffsets);

        subscriberMappedRepository.saveAll(mappedDataList);

        return brokerResponse;
    }

    private String extractUsername(String email) {
        return email.substring(0, email.indexOf('@'));
    }

    private int getLatestOffset(String username, String managerId) {
        SubscriberMapped latestData = subscriberMappedRepository.findFirstByUsernameAndManagerIdOrderByTimestampDesc(username, managerId);
        return latestData != null ? latestData.getOffset() : 0;
    }

    private List<PublishSectorOffset> getLatestOffsets(String username, List<SubscriberMapped> mappedDataList) {
        return mappedDataList.stream()
                .filter(mappedData -> mappedData.getOffset() != -1 && username.equals(mappedData.getUsername()))
                .collect(Collectors.toMap(
                        SubscriberMapped::getManagerId,
                        Function.identity(),
                        (existing, replacement) -> existing.getOffset() <= replacement.getOffset() ? existing : replacement
                ))
                .values()
                .stream()
                .map(mappedData -> new PublishSectorOffset(mappedData.getSector(), Math.max(mappedData.getOffset() - 1, 0)))
                .collect(Collectors.toList());
    }

    private String sendOffsetsToBroker(List<PublishSectorOffset> offsets) {
        try {
            String brokerUrl = "http://" + retrieveGlobalIpAddress() + "/fetchPublishedData";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<PublishSectorOffset>> requestEntity = new HttpEntity<>(offsets, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(brokerUrl, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return "Failed: " + response.getStatusCode();
            }
        } catch (Exception e) {
            return "Error processing external API request: " + e.getMessage();
        }
    }
}
