package com.example.seriium.network;

import com.example.seriium.models.Serie;
import com.example.seriium.models.SerieDetailsResponse;
import com.example.seriium.models.SerieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EpisodateApi {
    @GET("most-popular?")
    Call<SerieResponse> getMostPopular(@Query("page") int page);
    @GET("show-details?")
    Call<SerieDetailsResponse> getSerieDetails(@Query("q") int id);
    @GET("search?")
    Call<SerieResponse> getSearch(@Query("q") String text, @Query("page") int page);
}
