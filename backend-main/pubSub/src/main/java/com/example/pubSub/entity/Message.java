package com.example.pubSub.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Messages")
public class Message {

    @Id
    private String id;
    private String managerId;
    private String queue;
    private String message;
    private boolean broker;
    
    public Message() {
        this.broker = false;
        
    }

	public Message(String id, String publishManagerId, String queue, String message, boolean broker) {
		super();
		this.id = id;
		this.managerId = managerId;
		this.queue = queue;
		this.message = message;
		this.broker = broker;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getManagerId() {
		return managerId;
	}

	public void getManagerId(String managerId) {
		this.managerId = managerId;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isBroker() {
		return broker;
	}

	public void setBroker(boolean broker) {
		this.broker = broker;
	}

	@Override
	public String toString() {
		return "PublishMessage [id=" + id + ", managerId=" + managerId + ", queue=" + queue + ", message="
				+ message + ", broker=" + broker + "]";
	}
    
}
