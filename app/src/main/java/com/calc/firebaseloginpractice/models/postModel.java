package com.calc.firebaseloginpractice.models;

public class postModel
{
    private String userImage;
    private String username;
    private long postTime;
    private String postText;
    private String postImage;

    // to say if this post has image or no, we have to put this int type
    private int type;

    private String postId;


    public postModel(String userImage, String username, long postTime, String postText, String postImage, int type, String postId) {
        this.userImage = userImage;
        this.username = username;
        this.postTime = postTime;
        this.postText = postText;
        this.postImage = postImage;
        this.type = type;
        this.postId = postId;
    }

    public postModel() {
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
