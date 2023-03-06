package com.etackel.mapit;

public class Courses {

    // variables for storing our data.
    private String title, notes, latlng;

    public Courses() {
        // empty constructor
        // required for Firebase.
    }

    // Constructor for all variables.
    public Courses(String title, String notes, String latlng) {
        this.title = title;
        this.notes = notes;
        this.latlng = latlng;
    }

    // getter methods for all variables.
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    // setter method for all variables.
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }
}

