package com.calc.firebaseloginpractice.models;

public class roomsChatsModel
{
    private String roomsId;
    private String messageId;
    private String message;
    private String senderID;

    private long time;
    private int type;
    private String chatRoomUserName;
    private String chatRoomUserImage;

    public roomsChatsModel(String roomsId, String messageId, String message, String senderID,  long time, int type, String chatRoomUserName, String chatRoomUserImage) {
        this.roomsId = roomsId;
        this.messageId = messageId;
        this.message = message;
        this.senderID = senderID;

        this.time = time;
        this.type = type;
        this.chatRoomUserName = chatRoomUserName;
        this.chatRoomUserImage = chatRoomUserImage;
    }

    public roomsChatsModel() {
    }

    public String getRoomsId() {
        return roomsId;
    }

    public void setRoomsId(String roomsId) {
        this.roomsId = roomsId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
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

    public String getChatRoomUserName() {
        return chatRoomUserName;
    }

    public void setChatRoomUserName(String chatRoomUserName) {
        this.chatRoomUserName = chatRoomUserName;
    }

    public String getChatRoomUserImage() {
        return chatRoomUserImage;
    }

    public void setChatRoomUserImage(String chatRoomUserImage) {
        this.chatRoomUserImage = chatRoomUserImage;
    }
}
