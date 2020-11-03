package com.example.szakdoga.room_database;
/**
 * A RoomDatabase-ből leszármazó absztrakt osztály definiálása
 */
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = NoteModel.class,version = NoteDatabase.DATABASE_VERSION,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    //Adatbázis név és verzió
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="NOTE-Database-Room";

    private static NoteDatabase mInstance;

    //Adatbázis létrehozása
    public synchronized static NoteDatabase getInstance(Context context){
        if (mInstance==null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }

    public abstract NoteDao noteDao();
}
