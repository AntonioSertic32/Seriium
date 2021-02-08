package com.example.seriium.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserSerie implements Parcelable {

    private int serieID;
    private String nextEpisode;
    private int episodesWatched;
    private int episodesLeft;
    private String name;
    private String permalink;
    private String url;
    private String description;
    private String descriptionSource;
    private String startDate;
    private String endDate;
    private String country;
    private String status;
    private String runtime;
    private String network;
    private String youtubeLink;
    private String imagePath;
    private String thumbnail;
    private String rating;
    private String ratingCount;
    private ArrayList<String> genres;
    private List<SerieSeason> seasons;

    public UserSerie() {}

    public UserSerie(int serieID, String nextEpisode, int episodesWatched, int episodesLeft, String name, String permalink, String url, String description, String descriptionSource, String startDate, String endDate, String country, String status, String runtime, String network, String youtubeLink, String imagePath, String thumbnail, String rating, String ratingCount, ArrayList<String> genres, List<SerieSeason> seasons) {
        this.serieID = serieID;
        this.nextEpisode = nextEpisode;
        this.episodesWatched = episodesWatched;
        this.episodesLeft = episodesLeft;
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
        this.genres = genres;
        this.seasons = seasons;
    }

    protected UserSerie(Parcel in) {
        serieID = in.readInt();
        nextEpisode = in.readString();
        episodesWatched = in.readInt();
        episodesLeft = in.readInt();
        name = in.readString();
        permalink = in.readString();
        url = in.readString();
        description = in.readString();
        descriptionSource = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        country = in.readString();
        status = in.readString();
        runtime = in.readString();
        network = in.readString();
        youtubeLink = in.readString();
        imagePath = in.readString();
        thumbnail = in.readString();
        rating = in.readString();
        ratingCount = in.readString();
        genres = in.createStringArrayList();
    }

    public static final Creator<UserSerie> CREATOR = new Creator<UserSerie>() {
        @Override
        public UserSerie createFromParcel(Parcel in) {
            return new UserSerie(in);
        }

        @Override
        public UserSerie[] newArray(int size) {
            return new UserSerie[size];
        }
    };

    public int getSerieID() {
        return serieID;
    }

    public void setSerieID(int serieID) {
        this.serieID = serieID;
    }

    public String getNextEpisode() {
        return nextEpisode;
    }

    public void setNextEpisode(String nextEpisode) {
        this.nextEpisode = nextEpisode;
    }

    public int getEpisodesWatched() {
        return episodesWatched;
    }

    public void setEpisodesWatched(int episodesWatched) {
        this.episodesWatched = episodesWatched;
    }

    public int getEpisodesLeft() {
        return episodesLeft;
    }

    public void setEpisodesLeft(int episodesLeft) {
        this.episodesLeft = episodesLeft;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionSource() {
        return descriptionSource;
    }

    public void setDescriptionSource(String descriptionSource) {
        this.descriptionSource = descriptionSource;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public List<SerieSeason> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<SerieSeason> seasons) {
        this.seasons = seasons;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(serieID);
        dest.writeString(nextEpisode);
        dest.writeInt(episodesWatched);
        dest.writeInt(episodesLeft);
        dest.writeString(name);
        dest.writeString(permalink);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeString(descriptionSource);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(country);
        dest.writeString(status);
        dest.writeString(runtime);
        dest.writeString(network);
        dest.writeString(youtubeLink);
        dest.writeString(imagePath);
        dest.writeString(thumbnail);
        dest.writeString(rating);
        dest.writeString(ratingCount);
        dest.writeStringList(genres);
    }
}
