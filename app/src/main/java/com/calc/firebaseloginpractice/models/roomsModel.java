package com.calc.firebaseloginpractice.models;

import android.widget.EditText;

public class roomsModel
{

    private String roomName;
    private String roomCreator;
    private String roomId;

    public roomsModel(String roomName, String roomCreator, String roomId) {
        this.roomName = roomName;
        this.roomCreator = roomCreator;
        this.roomId = roomId;
    }



    public roomsModel() {
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomCreator() {
        return roomCreator;
    }

    public void setRoomCreator(String roomCreator) {
        this.roomCreator = roomCreator;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
