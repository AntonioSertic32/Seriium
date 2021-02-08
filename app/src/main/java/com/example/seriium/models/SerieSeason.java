package com.example.seriium.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SerieSeason {

    private String name;
    private boolean watched;
    private int numOfEpisodes;
    private List<SerieEpisodes> episodes;

    public SerieSeason() {}

    public SerieSeason(String name, boolean watched, int numOfEpisodes, List<SerieEpisodes> episodes) {
        this.name = name;
        this.watched = watched;
        this.numOfEpisodes = numOfEpisodes;
        this.episodes = episodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public int getNumOfEpisodes() {
        return numOfEpisodes;
    }

    public void setNumOfEpisodes(int numOfEpisodes) {
        this.numOfEpisodes = numOfEpisodes;
    }

    public List<SerieEpisodes> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<SerieEpisodes> episodes) {
        this.episodes = episodes;
    }
}
