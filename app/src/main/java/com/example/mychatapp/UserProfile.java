package com.example.mychatapp;

public class UserProfile {


    public String username,userID;

    public UserProfile() {
    }

    public UserProfile(String username, String userID) {
        this.username = username;
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
