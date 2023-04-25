package com.example.mychatapp;

public class Messages {
    String message;
    String senderId;
    long timestmp;
    String currenttime;

    public Messages(String message, String senderId, long timestmp, String currenttime) {
        this.message = message;
        this.senderId = senderId;
        this.timestmp = timestmp;
        this.currenttime = currenttime;
    }

    public Messages() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(long timestmp) {
        this.timestmp = timestmp;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }
}
