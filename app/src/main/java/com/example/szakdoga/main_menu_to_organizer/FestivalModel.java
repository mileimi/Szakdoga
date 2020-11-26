package com.example.szakdoga.main_menu_to_organizer;

/**
 * Fesztivál modell: ID, név, dátum, helyszín
 */
public class FestivalModel {
    private String id;
    String name;
    String date;
    String place;

    //Konstruktorok
    public FestivalModel() {
    }

    public FestivalModel(String name, String date,String id,String place) {
        this.name = name;
        this.date = date;
        this.place=place;
        this.id=id;
    }

    //Getterek és setterek
    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
