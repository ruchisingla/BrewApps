package com.example.brewapps.Local;

import java.util.List;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface MoviesDao {

    @Query("select * from movies")
    Flowable<List<Movie>> getMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("delete from movies")
    void deleteAllMovies();


}
