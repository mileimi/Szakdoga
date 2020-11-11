package com.example.szakdoga.main_menu_to_organizer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class EventModel implements Parcelable {
    private String ID;
    private String imagePath;
    private String title;
    private String time;
    private String description;
    private GeoPoint geoPoint;

    private EventModel() {}



    public EventModel(String title, String time){
        this.title=title;
        this.time=time;
        this.geoPoint=null;
    }



    public EventModel(String ID,String imagePath, String title, String time, GeoPoint geoPoint, String description){
        this.ID=ID;
        this.imagePath=imagePath;
        this.title=title;
        this.time=time;
        this.geoPoint=geoPoint;
        this.description=description;
    }
    public EventModel(String ID,String title, String time, String description){
        this.ID=ID;
        this.title=title;
        this.time=time;
        this.geoPoint=null;
        this.description=description;
    }

    EventModel(String title,String time,GeoPoint geoPoint){
        this.title=title;
        this.time=time;
        this.geoPoint=geoPoint;
    }

    protected EventModel(Parcel in) {
        ID = in.readString();
        title = in.readString();
        time = in.readString();
        description=in.readString();
        imagePath=in.readString();
    }

    public static final Creator<EventModel> CREATOR = new Creator<EventModel>() {
        @Override
        public EventModel createFromParcel(Parcel in) {
            return new EventModel(in);
        }

        @Override
        public EventModel[] newArray(int size) {
            return new EventModel[size];
        }
    };


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(title);
        dest.writeString(time);
        dest.writeString(description);
        dest.writeString(imagePath);
    }
}
