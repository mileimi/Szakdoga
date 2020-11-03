package com.example.szakdoga.room_database;
/**
 * A jegyzet modell leírása,
 * entitás-az adatbázis sémáját adja meg
 */

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.io.Serializable;

//tábla név-minden entitás egy sor az adatbázisban
@Entity(tableName = "notes")
public class NoteModel {

    //oszlopok definiálása
    //automatikusan generáló egyedi id
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "priority")
    private int priority;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "description")
    private String description;

    public NoteModel(){

    }

    @Ignore
    public NoteModel(String title, int priority, String date, String description){
        this.title=title;
        this.priority=priority;
        this.date=date;
        this.description=description;
    }
    //setterek és getterek a változókhoz
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
