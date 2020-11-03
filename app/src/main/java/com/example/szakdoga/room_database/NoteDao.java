package com.example.szakdoga.room_database;
/**
 * Interfész, melyben a lekérdező metódusokat definiáltuk
 */

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface NoteDao {

    //ID alapján kiválasztott jegyzet kérése
    @Query("SELECT * FROM notes WHERE id=:noteId")
    List<NoteModel> getNoteById(int noteId);

    //Minden jegyzet kérése
    @Query("SELECT * FROM notes")
    List<NoteModel> getAllNotes();

    //Jegyzet beszúrása
    @Insert(onConflict = REPLACE)
    void insertNote(NoteModel noteModel);

    //Módosítás
    @Update
    void updateNote(NoteModel noteModel);

    //Jegyzet módosítás
    @Query("UPDATE notes SET title=:sText, description=:sDescription,priority=:sPriority,date=:sDate WHERE id= :sID")
    void update(int sID,String sText,String sDescription,int sPriority,String sDate);

    //Adott jegyzet törlése
    @Delete
    void deleteNote(NoteModel noteModel);

}
