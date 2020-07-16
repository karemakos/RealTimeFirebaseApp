package com.calc.firebaseloginpractice.models;

public class roomRequestModel
{
    private String roomName;
    private String roomOwner;
    private String roomID;
    private String ownerID;
    private String requestID;


    public roomRequestModel(String roomName, String roomOwner, String roomID, String ownerID, String requestID) {
        this.roomName = roomName;
        this.roomOwner = roomOwner;
        this.roomID = roomID;
        this.ownerID = ownerID;
        this.requestID = requestID;
    }

    public roomRequestModel() {
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(String roomOwner) {
        this.roomOwner = roomOwner;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }
}
