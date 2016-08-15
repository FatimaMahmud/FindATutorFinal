package com.exception.findatutor.Activities;

/**
 * The Class Conversation is a Java Bean class that represents a single chat
 * conversation message.
 */
public class UserInfo {
    private String uname;
    private String lat;
    private String lng;
    private String status;
    private String gender;
    private String age;
    private String notification;


    public UserInfo(String uname, String lat, String lng, String notification) {
        this.uname = uname;
        this.lat = lat;
        this.lng = lng;
        this.notification = notification;
        this.status = status;
        this.gender = gender;
        this.age = age;
    }

    public String getName() {
        return uname;
    }

    public String getStatus() {
        return status;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getNotification() {
        return notification;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

}

