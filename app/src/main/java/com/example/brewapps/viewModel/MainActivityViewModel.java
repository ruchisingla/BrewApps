package com.example.brewapps.viewModel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.brewapps.Local.Movie;
import com.example.brewapps.R;
import com.example.brewapps.remote.MovieDBResponse;
import com.example.brewapps.repository.LocalRepository;
import com.example.brewapps.service.ApiClient;
import com.example.brewapps.service.MovieApiService;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.room.RoomDatabase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivityViewModel extends AndroidViewModel {

    private LocalRepository localRepository;


    private CompositeDisposable compositeDisposable=new CompositeDisposable();

    private RoomDatabase db;
    private Application application;

    private Observable<MovieDBResponse> movieDBResponseObservable;

    public MainActivityViewModel(Application application) {
        super(application);
        this.application=application;
        this.localRepository=new LocalRepository(application);

    }
public void deleteMovie(Movie movie)
{
    localRepository.deleteMovie(movie);
}
   public Flowable<List<Movie>> getMovies()
   {
       return localRepository.getMovies();
   }
   public void insertMovie(Movie movie)
   {
       localRepository.insertMovie(movie);
   }

   public void getMoviesFromApi()
   {
       MovieApiService apiService = ApiClient.getClient();
       //Log.d("ModelVeiw","In getNowplayingMovies() 1");
       movieDBResponseObservable =apiService.getNowPlayingMovies(application.getApplicationContext().getString(R.string.api_key));
       compositeDisposable.add(movieDBResponseObservable.subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .flatMap((Function<MovieDBResponse, ObservableSource<Movie>>) movieDBResponse -> Observable.fromArray(movieDBResponse.getMovies().toArray(new Movie[0])))
               .subscribeWith(new DisposableObserver<Movie>() {
                   @Override
                   public void onNext(@NonNull Movie movie) {
                       //Movie movie1 = new Movie(movie.getAdult(),movie.getBackdropPath(),movie.getGenreIds(),movie.getId(),movie.getOriginalLanguage(),movie.getOriginalTitle(),movie.getOverview(),movie.getPopularity(),movie.getPosterPath(),movie.getReleaseDate(),movie.getTitle(),movie.getVideo(),movie.getVoteAverage(),movie.getVoteCount());
                       Log.d("ViewModel","Movie i got is ="+movie.toString());
                       //movies.add(movie);
                       insertMovie(movie);

                   }

                   @Override
                   public void onError(@NonNull Throwable error) {
                       Log.d("viewmodel","onerror geting movies from api ="+error.getCause());
                       if (error instanceof SocketTimeoutException)
                       {
                           // "Connection Timeout";
                           Toast.makeText(application,"Network Error.Check your internet",Toast.LENGTH_LONG).show();
                       }
                       else if (error instanceof IOException)
                       {
                           // "Timeout";
                           Toast.makeText(application,"Network Error.Check your internet",Toast.LENGTH_LONG).show();
                       }

                   }

                   @Override
                   public void onComplete() {
                       Log.d("viewmodel", "oncomplete");

                      // mutableLiveData.postValue(movies);


                   }
               }));

   }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
        compositeDisposable.clear();
    }
}
