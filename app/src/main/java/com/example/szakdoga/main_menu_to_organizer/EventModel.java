package com.example.szakdoga.main_menu_to_organizer;

import com.google.firebase.firestore.GeoPoint;
/**
 * Az esemény adatainak tárolására szolgál az alábbi model:
 * Tartalmazza az esemény ID-t,hátterének elérési útját, címét, leírását, dátumát, koordinátáit
 */

public class EventModel {
    private String ID;
     String Image;
     String Title;
     String Time;
     String Description;
     GeoPoint GeoPoint;

    public EventModel(String ID, String image, String title, String time, String description, com.google.firebase.firestore.GeoPoint geoPoint) {
        this.ID = ID;
        Image = image;
        Title = title;
        Time = time;
        Description = description;
        GeoPoint = geoPoint;
    }

    public EventModel() {
    }

    public EventModel(String title, com.google.firebase.firestore.GeoPoint geoPoint) {
        Title = title;
        GeoPoint = geoPoint;
    }



    //Getterek, setterek
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public com.google.firebase.firestore.GeoPoint getGeoPoint() {
        return GeoPoint;
    }

    public void setGeoPoint(com.google.firebase.firestore.GeoPoint geoPoint) {
        GeoPoint = geoPoint;
    }
}
