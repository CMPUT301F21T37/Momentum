package com.example.momentum.habitEvents;

import android.net.Uri;

import java.io.Serializable;

/**
 * A class that stores information stored from HabitEventsFragment for easy access and initialization.
 * @author Kaye Ena Crayzhel F. Misay
 * @author Han Yan
 */
public class Event implements Serializable {
    private String title;
    private String comment;
    private double latitude;
    private double longitude;
    private String imageUriStr;


    public Event(){

    }

    public Event (String title, String comment, double latitude, double longitude, String imageUriStr){
        this.title = title;
        this.comment = comment;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Getter for Events title
     * @return
     * A string of the title.
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Getter for Events comment
     * @return
     * A string of the comment.
     */
    public String getComment() {
        return comment;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUriStr() {
        return imageUriStr;
    }

    public void setImageUriStr(String imageUriStr) {
        this.imageUriStr = imageUriStr;
    }
}