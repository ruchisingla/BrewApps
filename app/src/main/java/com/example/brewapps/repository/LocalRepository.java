package com.example.brewapps.repository;

import com.example.brewapps.Local.Movie;


import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;


import android.app.Application;
import android.util.Log;

import com.example.brewapps.Local.MoviesDao;
import com.example.brewapps.Local.RoomMoviesDb;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocalRepository {
    private MoviesDao moviesDao;
    private static final String TAG = LocalRepository.class.getSimpleName();
    private RoomMoviesDb roomMoviesDb;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LocalRepository(Application application) {
        roomMoviesDb=RoomMoviesDb.getInstance(application);
        this.moviesDao = roomMoviesDb.getMoviesDao();


    }

    public void deleteMovie(Movie movie)
    {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Throwable {
                moviesDao.deleteMovie(movie);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG,"Element deleted");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                     Log.d(TAG,"Element not deleted");
                    }
                });
    }
    public Flowable<List<Movie>> getMovies() {
        return moviesDao.getMovies();
    }



    public void insertMovie(Movie movie) {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Throwable {
                long id=  moviesDao.insertMovie(movie);
               // Log.d("Localrepository","inserted id is"+id);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        //Log.d("Localrepository","insertion completed");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Localrepository","insertion error"+e.getCause());
                    }
                });

    }

}



