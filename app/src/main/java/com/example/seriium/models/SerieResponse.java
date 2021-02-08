package com.example.seriium.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SerieResponse {
    @SerializedName("page")
    private int page;

    @SerializedName("pages")
    private int totalPages;

    @SerializedName("tv_shows")
    private List<Serie> tvShows;

    public SerieResponse(int page, int totalPages, List<Serie> tvShows) {
        this.page = page;
        this.totalPages = totalPages;
        this.tvShows = tvShows;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Serie> getTvShows() {
        return tvShows;
    }

    public void setTvShows(List<Serie> tvShows) {
        this.tvShows = tvShows;
    }
}