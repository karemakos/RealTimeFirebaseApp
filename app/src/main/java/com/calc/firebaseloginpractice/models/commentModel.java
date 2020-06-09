package com.calc.firebaseloginpractice.models;

public class commentModel
{
    private String senderId;
    private String message;
    private String userName;
    private String userImage;
    private long time;
    private int type;
    private String id;


    public commentModel(String senderId, String message, String userName, String userImage, long time, int type, String id) {
        this.senderId = senderId;
        this.message = message;
        this.userName = userName;
        this.userImage = userImage;
        this.time = time;
        this.type = type;
        this.id = id;
    }

    public commentModel() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
