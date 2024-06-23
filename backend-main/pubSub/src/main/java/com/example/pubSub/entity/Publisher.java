package com.example.pubSub.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "publishers")
public class Publisher {

    @Id
    private String id;
    private String userName;
    private String password;
    private Date timeStamp;
    private boolean isEnable;

    public Publisher(String username, String password) {
        this.userName = username;
        this.password = password;
        this.timeStamp = new Date(); 
        this.isEnable = true; 
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public Date getTimestamp() {
        return timeStamp;
    }

    public void setTimestamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    @Override
    public String toString() {
        return "Publisher{" +
                "id='" + id + '\'' +
                ", username='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", timestamp=" + timeStamp +
                ", isEnable=" + isEnable +
                '}';
    }
}
