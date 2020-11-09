package com.example.szakdoga.main_menu_to_organizer;

import com.google.firebase.firestore.GeoPoint;

public class EventModel {
    private String ID;
    private String title;
    private String time;
    private GeoPoint geoPoint;

    private EventModel() {}

    public EventModel(String title, String time){
        this.title=title;
        this.time=time;
        this.geoPoint=null;
    }

    public EventModel(String ID,String title, String time){
        this.ID=ID;
        this.title=title;
        this.time=time;
        this.geoPoint=null;
    }

    EventModel(String title,String time,GeoPoint geoPoint){
        this.title=title;
        this.time=time;
        this.geoPoint=geoPoint;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
