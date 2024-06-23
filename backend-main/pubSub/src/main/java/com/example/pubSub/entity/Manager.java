package com.example.pubSub.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@Document(collection = "Managers")
public class Manager {
    @Id
    private String id;

    @Field(name = "Queue")
    private String queue;

    private Date timeStamp;
    private boolean isEnable;

    public Manager(String queue) {
        this.queue = queue;
        this.timeStamp = new Date();
        this.isEnable = true;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setisEnable(boolean flag) {
		this.isEnable = flag;
	}

	@Override
	public String toString() {
		return "PublishManager [id=" + id + ", queue=" + queue + ", timeStamp=" + timeStamp + ", isEnable=" + isEnable + "]";
	}

    
    
}
