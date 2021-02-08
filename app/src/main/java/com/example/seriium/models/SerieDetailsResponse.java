package com.example.seriium.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SerieDetailsResponse {
    @SerializedName("tvShow")
    private SerieDetails tvShow;

    public SerieDetailsResponse(SerieDetails tvShow) {
        this.tvShow = tvShow;
    }

    public SerieDetails getTvShow() {
        return tvShow;
    }

    public void setTvShow(SerieDetails tvShow) {
        this.tvShow = tvShow;
    }
}