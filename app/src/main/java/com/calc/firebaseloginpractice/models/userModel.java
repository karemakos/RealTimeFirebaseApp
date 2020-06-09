package com.calc.firebaseloginpractice.models;

public class userModel
{
    private String name;
    private String email;
    private String mobile;
    private String address;
    private String imageUri;
    private String Uid;


    public userModel(String name, String email, String mobile, String address, String imageUri, String Uid) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.imageUri = imageUri;
        this.Uid = Uid;
    }


    // we have to create empty constructor
    public userModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
