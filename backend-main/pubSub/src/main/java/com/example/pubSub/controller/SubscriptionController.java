package com.example.pubSub.controller;

import com.example.pubSub.entity.UserSubscriber;
import com.example.pubSub.model.ResponseMessage;
import com.example.pubSub.service.SubscriptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/subscribers")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    // Endpoint to register a new subscriber
    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody UserSubscriber user) {
        String userName = user.getUsername();
        String userPassword = user.getPassword();

        // Check if the user already exists
        if (subscriptionService.findUserByUsername(userName) != null) {
            return new ResponseEntity<>(
                    new ResponseMessage(HttpStatus.CONFLICT.value(), "Error: Username already in use"),
                    HttpStatus.CONFLICT);
        }

        // Create and save new subscriber
        UserSubscriber newUser = new UserSubscriber(userName, userPassword);
        subscriptionService.saveUser(newUser);

        return new ResponseEntity<>(
                new ResponseMessage(HttpStatus.CREATED.value(), "User successfully registered!"),
                HttpStatus.CREATED);
    }

    // Endpoint to validate subscriber credentials
    @PostMapping("/validate")
    public ResponseEntity<ResponseMessage> validateUser(@RequestBody UserSubscriber user) {
        String userName = user.getUsername();
        String userPassword = user.getPassword();

        if (subscriptionService.areCredentialsValid(userName, userPassword)) {
            return new ResponseEntity<>(
                    new ResponseMessage(HttpStatus.OK.value(), "Valid credentials!"),
                    HttpStatus.OK);
        } else {
            UserSubscriber existingUser = subscriptionService.findUserByUsername(userName);
            if (existingUser != null) {
                return new ResponseEntity<>(
                        new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Invalid password"),
                        HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(
                        new ResponseMessage(HttpStatus.NOT_FOUND.value(), "User not found"),
                        HttpStatus.NOT_FOUND);
            }
        }
    }
}
