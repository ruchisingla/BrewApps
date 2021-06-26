package com.example.brewapps.service;

import com.example.brewapps.remote.MovieDBResponse;


import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApiService {

    @GET("movie/now_playing")
    Observable<MovieDBResponse> getNowPlayingMovies(@Query("api_key") String apiKey);
}
