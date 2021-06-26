package com.example.brewapps.Local;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Movie.class},version = 1,exportSchema = false)
@TypeConverters(DataConverters.class)
public abstract class RoomMoviesDb extends RoomDatabase {
    private static RoomMoviesDb sInstance;
    private static final String LOG_TAG = Application.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movies_database";

    public abstract MoviesDao getMoviesDao();
    public static RoomMoviesDb getInstance(Context context)
    {
        if(sInstance==null)
        {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context, RoomMoviesDb.class, DATABASE_NAME)
                        .fallbackToDestructiveMigration().build();
            }

        }
        return sInstance;
    }


}
