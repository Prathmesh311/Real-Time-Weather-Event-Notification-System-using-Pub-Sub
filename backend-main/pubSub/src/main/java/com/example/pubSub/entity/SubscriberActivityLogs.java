package com.example.pubSub.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.example.pubSub.repository.SubscribeMessageRepository;

import org.springframework.data.annotation.Transient;

import java.util.HashMap;
import java.util.Map;


@Document(collection = "subscriberActivityLogs")
public class SubscriberActivityLogs {

    @Id
    private String id;
    private String publishManagerId; 
    private String publishedDataId;
    private String queue;
    private String message;
    private String subscriberName;
    private Long offset;
    private boolean isEnable;
    private long timeStamp;

    // store offset values associated with publishMasterId
    @Field("offsets")
    private Map<String, Long> offsets = new HashMap<>();

    @Transient
    private transient SubscribeMessageRepository subscribeMessageRepository;
    
    
    public SubscriberActivityLogs() {
    }
    
    public SubscriberActivityLogs(String id, String publishManagerId, String publishedDataId, String queue, String message,
			String subscriberName, Long offset, boolean isEnable, long timeStamp, Map<String, Long> offsets) {

		this.id = id;
		this.publishManagerId = publishManagerId;
		this.publishedDataId = publishedDataId;
		this.queue = queue;
		this.message = message;
		this.subscriberName = subscriberName;
		this.offset = offset;
		this.isEnable = isEnable;
		this.timeStamp = timeStamp;
	}

 

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPublishManagerId() {
		return publishManagerId;
	}

	public void setPublishManagerId(String publishManagerId) {
		this.publishManagerId = publishManagerId;
	}

	public String getPublishedDataId() {
		return publishedDataId;
	}

	public void setPublishedDataId(String publishedDataId) {
		this.publishedDataId = publishedDataId;
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

	public String getSubscriberName() {
		return subscriberName;
	}

	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Map<String, Long> getOffsets() {
		return offsets;
	}

	public void setOffsets(Map<String, Long> offsets) {
		this.offsets = offsets;
	}


	
	public void offsetSetter() {
        Long latestOffsetForManagerId = getLatestOffset(publishManagerId);
    
        //Set new offset
        Long currOffset = latestOffsetForManagerId + 1;
        this.offset = currOffset;
        offsets.put(publishManagerId, currOffset);
    }
    	
	//fetch newest offset value
    private Long getLatestOffset(String publishManagerId) {
        Long latestOffset = subscribeMessageRepository.findMaxOffsetByPublishManagerId(publishManagerId);
        return latestOffset != null ? latestOffset : 0L;
    }


	@Override
	public String toString() {
		return "SubscriberActivityLogs [id=" + id + ", publishManagerId=" + publishManagerId + ", publishedDataId="
				+ publishedDataId + ", queue=" + queue + ", message=" + message + ", subscriberName=" + subscriberName
				+ ", offset=" + offset + ", isEnable=" + isEnable + ", timeStamp=" + timeStamp + ", offsets=" + offsets
				+ "]";
	}

}
