package com.example.seriium.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static final String BASE_URL = "https://www.episodate.com/api/";
    private static RetrofitManager instance;
    private EpisodateApi service;

    private RetrofitManager(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(EpisodateApi.class);
    }

    public static RetrofitManager getInstance(){
        if(instance == null){
            instance = new RetrofitManager();
        }
        return instance;
    }

    public EpisodateApi service (){
        return service;
    }
}
