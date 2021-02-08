package com.example.seriium.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seriium.R;
import com.example.seriium.adapters.EpisodesAdapter;
import com.example.seriium.adapters.SeasonsAdapter;
import com.example.seriium.listeners.OnSeasonClick;
import com.example.seriium.listeners.OnSeasonNotAddedClick;
import com.example.seriium.models.SerieDetails;
import com.example.seriium.models.SerieEpisodes;
import com.example.seriium.models.SerieSeason;
import com.example.seriium.models.UserSerie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Seasons extends AppCompatActivity {

    public static String SEASON="serie_id";
    public static String SERIEKEY="serie_key";
    private int Key;
    private int season_num;
    private boolean not_added = false;
    private TextView season_name;
    private SerieDetails serieDetails;
    private UserSerie SerieDetails;
    private List<SerieEpisodes> currentSeasonEpisodes = new ArrayList<>();
    private Button go_back;
    private EpisodesAdapter episodesAdapter;
    public RecyclerView episodesRecyclerView;
    public FirebaseAuth mAuth;
    int seasonNumDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons);

        Key = getIntent().getIntExtra(SERIEKEY, Key);

        mAuth = FirebaseAuth.getInstance();

        episodesRecyclerView = findViewById(R.id.serie_episodes);
        episodesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        go_back = findViewById(R.id.goBackButton);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });

        // Ispisivanje otvorene sezone
        season_num = getIntent().getIntExtra(SEASON,season_num);
        season_name = findViewById(R.id.season_name);
        season_name.setText("Season " + season_num);

        // Dohvacanje detalja serije
        Gson gson = new Gson();

        String serie_episodes = GetData("serie_episodes");
        if(serie_episodes != null) {
            serieDetails = gson.fromJson(serie_episodes, SerieDetails.class);
            not_added = false;
        }

        String serie_db_episodes = GetData("serie_db_episodes");
        if(serie_db_episodes != null) {
            SerieDetails = gson.fromJson(serie_db_episodes, UserSerie.class);
            not_added = true;

            String isStillAdded = GetData("isStillAdded");
            if(isStillAdded != null) {
                not_added = true;
            }
            else {
                not_added = false;
            }
        }

        //Filtriranje
        if(not_added) {
            currentSeasonEpisodes = SerieDetails.getSeasons().get(season_num - 1).getEpisodes();
        }
        else {
            for(SerieEpisodes currentEpisode : serieDetails.getEpisodes()) {
                if(currentEpisode.getSeason() == season_num){
                    currentEpisode.setAir_date( currentEpisode.getAir_date().substring(0, 10));
                    currentSeasonEpisodes.add(currentEpisode);
                }
            }
        }
        //Ispis epizoda
        //Toast.makeText(this, "Broj epizoda: " + currentSeasonEpisodes.size(), Toast.LENGTH_SHORT).show();
        initializeRecyclerView(currentSeasonEpisodes, not_added);
    }

    private String GetData (String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", 0);
        if(sharedPreferences.contains(key)){
            String json = sharedPreferences.getString(key, null);
            return json;
        }
        else {
            return  null;
        }
    }

    void initializeRecyclerView(List<SerieEpisodes> episodes, boolean not_added){
        //Toast.makeText(this, "not_added: " + not_added, Toast.LENGTH_SHORT).show();
        episodesAdapter = new EpisodesAdapter(episodes, not_added, mClick);
        episodesRecyclerView.setAdapter(episodesAdapter);
    }

    OnSeasonClick mClick = new OnSeasonClick() {
        @Override
        public void onClick(SerieSeason season) {

        }

        @Override
        public void onSeasonCheck(int position) {
            // Toast.makeText(Seasons.this, "Kliknuo.. " + position, Toast.LENGTH_SHORT).show();
            seasonNumDb = season_num - 1;
            DatabaseReference episode = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key + "/seasons/" + seasonNumDb + "/episodes/" + position);
            episode.child("watched").setValue(!SerieDetails.getSeasons().get(seasonNumDb).getEpisodes().get(position).isWatched());
            SerieDetails.getSeasons().get(seasonNumDb).getEpisodes().get(position).setWatched(!SerieDetails.getSeasons().get(seasonNumDb).getEpisodes().get(position).isWatched());
            episodesAdapter.notifyItemChanged(position);

            // Provjere
            //      1. Kad se odznaci da odznaci i sezonu
            //      2. Kad se oznaci epizoda da provjeri ostale ep te sezone i ak su sve true da stavi sezonu u true

            //Update -> episodesWatched; episodesLeft; nextEpisode;
            DatabaseReference updateInfoDb = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key);

            if(!SerieDetails.getSeasons().get(seasonNumDb).getEpisodes().get(position).isWatched()) {
                DatabaseReference season = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key + "/seasons/" + seasonNumDb);
                season.child("watched").setValue(false);
                SerieDetails.getSeasons().get(seasonNumDb).setWatched(false);

                updateInfoDb.child("episodesWatched").setValue(SerieDetails.getEpisodesWatched() - 1);
                SerieDetails.setEpisodesWatched(SerieDetails.getEpisodesWatched() - 1);
                updateInfoDb.child("episodesLeft").setValue(SerieDetails.getEpisodesLeft() + 1);
                SerieDetails.setEpisodesLeft(SerieDetails.getEpisodesLeft() + 1);
            }
            else {
                boolean isAllWatched = true;
                for(SerieEpisodes episodeDb : SerieDetails.getSeasons().get(seasonNumDb).getEpisodes()) {
                    if(!episodeDb.isWatched()){
                        isAllWatched = false;
                    }
                }
                if(isAllWatched){
                    DatabaseReference season = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key + "/seasons/" + seasonNumDb);
                    season.child("watched").setValue(true);
                    SerieDetails.getSeasons().get(seasonNumDb).setWatched(true);
                }

                updateInfoDb.child("episodesWatched").setValue(SerieDetails.getEpisodesWatched() + 1);
                SerieDetails.setEpisodesWatched(SerieDetails.getEpisodesWatched() + 1);
                updateInfoDb.child("episodesLeft").setValue(SerieDetails.getEpisodesLeft() - 1);
                SerieDetails.setEpisodesLeft(SerieDetails.getEpisodesLeft() - 1);
            }

            updateInfoDb.child("nextEpisode").setValue(GetNextEpisode());
            SerieDetails.setNextEpisode(GetNextEpisode());

            go_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("whichSeason", seasonNumDb);
                    Gson gson = new Gson();
                    String json2 = gson.toJson(SerieDetails);
                    intent.putExtra("updatedSerieDetails", json2);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    };

    private String GetNextEpisode() {
        String nextEpisodeDb = "";
        for(SerieSeason seasonHelper : SerieDetails.getSeasons() ) {
            for (SerieEpisodes episodeHelper : seasonHelper.getEpisodes()){
                if(!episodeHelper.isWatched()){
                    nextEpisodeDb = "S" + episodeHelper.getSeason() + " E" + episodeHelper.getEpisode();
                    break;
                }
            }
            if(!nextEpisodeDb.isEmpty()) {
                break;
            }
        }
        return nextEpisodeDb;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("whichSeason", seasonNumDb);
        Gson gson = new Gson();
        String json2 = gson.toJson(SerieDetails);
        intent.putExtra("updatedSerieDetails", json2);
        setResult(RESULT_OK, intent);
        finish();
    }
}