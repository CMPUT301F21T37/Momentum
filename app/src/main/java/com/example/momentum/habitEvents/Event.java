package com.example.momentum.habitEvents;

import android.net.Uri;

import java.io.Serializable;

/**
 * A class that stores information stored from HabitEventsFragment for easy access and initialization.
 *
 * @author Kaye Ena Crayzhel F. Misay
 * @author Han Yan
 * @author Mohammed Alzafarani
 */
public class Event implements Serializable {

    // class vars
    private String title;
    private String comment;
    private double latitude;
    private double longitude;
    private String imageName;


    /**
     * This constructor constructs a new Event Object
     */
    public Event() {

    }

    /**
     * This constructor constructs an event object with a title, comment, lat, and long
     *
     * @param title     The title of the event
     * @param comment   The comment of the event
     * @param latitude  The latitude of the event
     * @param longitude The longitude of the event
     * @param imageName The file name of an image
     */
    public Event(String title, String comment, double latitude, double longitude, String imageName) {
        this.title = title;
        this.comment = comment;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageName = imageName;
    }

    /**
     * Getter for Events title
     *
     * @return A string of the title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Getter for Events comment
     *
     * @return A string of the comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * This method sets the title of the event
     *
     * @param title A string of the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method sets the comment of the event
     *
     * @param comment A string of the comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Getter for Events latitude
     *
     * @return A double latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * This method sets the latitude of the event
     *
     * @param latitude A double latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter for Events longitude
     *
     * @return A double longitude.
     */
    public double getLongitude() { return longitude; }

    /**
     * This method sets the longitude of the event
     *
     * @param longitude A double longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageName() { return imageName; }

    public void setImageName(String imageName) { this.imageName = imageName; }
}