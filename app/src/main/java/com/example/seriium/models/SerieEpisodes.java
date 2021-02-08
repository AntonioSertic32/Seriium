package com.example.seriium.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SerieEpisodes implements Parcelable {

    @SerializedName("season")
    private int season;

    @SerializedName("episode")
    private int episode;

    @SerializedName("name")
    private String name;

    @SerializedName("air_date")
    private String air_date;

    private boolean watched;

    protected SerieEpisodes () {}

    protected SerieEpisodes(Parcel in) {
        season = in.readInt();
        episode = in.readInt();
        name = in.readString();
        air_date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(season);
        dest.writeInt(episode);
        dest.writeString(name);
        dest.writeString(air_date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SerieEpisodes> CREATOR = new Creator<SerieEpisodes>() {
        @Override
        public SerieEpisodes createFromParcel(Parcel in) {
            return new SerieEpisodes(in);
        }

        @Override
        public SerieEpisodes[] newArray(int size) {
            return new SerieEpisodes[size];
        }
    };

    public int getSeason() {
        return season;
    }

    public int getEpisode() {
        return episode;
    }

    public String getName() {
        return name;
    }

    public String getAir_date() {
        return air_date;
    }

    public void setAir_date(String air_date) {
        this.air_date = air_date;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    /*
    season": 1,
    "episode": 1,
    "name": "Pilot",
    "air_date": "2012-10-11 00:00:00"
    */
}
