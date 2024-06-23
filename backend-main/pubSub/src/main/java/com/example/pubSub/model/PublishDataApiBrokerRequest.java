package com.example.pubSub.model;

public class PublishDataApiBrokerRequest {
    private String queue;
    private String message;

    public PublishDataApiBrokerRequest() {
    }

    public PublishDataApiBrokerRequest(String queue, String message) {
        this.queue = queue;
        this.message = message;
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

	@Override
	public String toString() {
		return "PublishBrokerRequest [Queue=" + queue + ", message=" + message + "]";
	}

}
