package com.example.seriium.models;

import android.os.Parcelable;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerieDetails implements Serializable {

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("permalink")
    private String permalink;

    @SerializedName("url")
    private String url;

    @SerializedName("description")
    private String description;

    @SerializedName("description_source")
    private String descriptionSource;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("country")
    private String country;

    @SerializedName("status")
    private String status;

    @SerializedName("runtime")
    private String runtime;

    @SerializedName("network")
    private String network;

    @SerializedName("youtube_link")
    private String youtubeLink;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("image_thumbnail_path")
    private String thumbnail;

    @SerializedName("rating")
    private String rating;

    @SerializedName("rating_count")
    private String ratingCount;

    @SerializedName("genres")
    private ArrayList<String> genres;

    // TODO: Omogucit dodavanje i ova dva..
    /*
        @SerializedName("countdown")
        private String countdown;


        @SerializedName("pictures")
        private String pictures;
    */
    @SerializedName("episodes")
    private List<SerieEpisodes> episodes;

    public SerieDetails () { }

    public SerieDetails(int id, String name, String permalink, String url, String description, String descriptionSource, String startDate, String endDate, String country, String status, String runtime, String network, String youtubeLink, String imagePath, String thumbnail, String rating, String ratingCount, List<SerieEpisodes> episodes) {
        this.id = id;
        this.name = name;
        this.permalink = permalink;
        this.url = url;
        this.description = description;
        this.descriptionSource = descriptionSource;
        this.startDate = startDate;
        this.endDate = endDate;
        this.country = country;
        this.status = status;
        this.runtime = runtime;
        this.network = network;
        this.youtubeLink = youtubeLink;
        this.imagePath = imagePath;
        this.thumbnail = thumbnail;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.episodes = episodes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionSource() {
        return descriptionSource;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getCountry() {
        return country;
    }

    public String getStatus() {
        return status;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getNetwork() {
        return network;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getRating() {
        return rating;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public ArrayList<String> getGenres() { return genres; }

    public List<SerieEpisodes> getEpisodes() {
        return episodes;
    }
}
