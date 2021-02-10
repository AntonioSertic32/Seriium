package com.example.seriium.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

//import android.app.Fragment;
import androidx.fragment.app.Fragment;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seriium.R;
import com.example.seriium.adapters.SeasonsAdapter;
import com.example.seriium.adapters.SeasonsAdapterNotAdded;
import com.example.seriium.fragments.FourthFragment;
import com.example.seriium.fragments.SecondFragment;
import com.example.seriium.listeners.OnSeasonClick;
import com.example.seriium.listeners.OnSeasonNotAddedClick;
import com.example.seriium.models.SerieDetails;
import com.example.seriium.models.SerieDetailsResponse;
import com.example.seriium.models.SerieEpisodes;
import com.example.seriium.models.SerieSeason;
import com.example.seriium.models.UserSerie;
import com.example.seriium.network.RetrofitManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.seriium.activities.Seasons.SEASON;
import static com.example.seriium.activities.Seasons.SERIEKEY;


public class SerieDetail extends AppCompatActivity implements Callback<SerieDetailsResponse> {

    // Firebase
    public FirebaseAuth mAuth;
    private UserSerie serieFromDatabase = new UserSerie();

    // Preferences
    public static String SERIE_ID="serie_id";
    private int serieID;

    // Layout
    private TextView serieName;
    private TextView serieDescription;
    private TextView serieStatus;
    private TextView serieRuntime;
    private TextView serieNetwork;
    private TextView serieRate;
    private ImageView serieImage;
    private TextView serieGenre;

    private FloatingActionButton add_serie;
    private Button go_back;
    private ImageButton goToUser;

    // API
    private SerieDetails serieDetails;

    // Description expansion
    private String full_description;
    private boolean expanded = false;

    // Seasons below
    public RecyclerView seasonsRecyclerView;
    private SeasonsAdapter seasonsAdapter;
    private SeasonsAdapterNotAdded seasonsNotAddedAdapter;
    private List<SerieSeason> SeasonList = new ArrayList<>();
    private List<String> seasonList = new ArrayList<>();
    private int numberOfSeasons;

    // UserSerie
    private boolean isAlreadyInList = false;
    private int Key = -1;
    private int NextKey = -1;
    private List<Integer> listKeys = new ArrayList<>();

    public static final int REQUEST = 2;
    private YouTubePlayerView youTubePlayerView;
    private LinearLayout youtubeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serie_detail);

        SaveData("serie_db_episodes", null);
        SaveData("serie_episodes", null);
        SaveData("isStillAdded", "default");

        // Youtube
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        youtubeLayout = findViewById(R.id.youtubeLayout);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Layout
        serieID = getIntent().getIntExtra(SERIE_ID,serieID);
        serieName = findViewById(R.id.serie_name);
        serieStatus = findViewById(R.id.serie_status);
        serieRuntime = findViewById(R.id.serie_runtime);
        serieNetwork = findViewById(R.id.serie_network);
        serieRate = findViewById(R.id.serie_rate);
        serieImage = findViewById(R.id.serie_image);
        serieGenre = findViewById(R.id.serie_genre);

        add_serie = findViewById(R.id.add_button);

        go_back = findViewById(R.id.goBackButton);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        goToUser = findViewById(R.id.goToUser);
        goToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // POSTAVITI PREFERENCES I ZATVORIT ACTIVITY
                Intent intent = new Intent();
                intent.putExtra("openUser", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        serieDescription = findViewById(R.id.serie_description);
        serieDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!expanded){
                    serieDescription.setText(full_description);
                    expanded = true;
                }
                else {
                    serieDescription.setText(full_description.substring(0, 150) + "...");
                    expanded = false;
                }
            }
        });

        // Seasons below
        seasonsRecyclerView = findViewById(R.id.serie_seasons);
        seasonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // API
        Call<SerieDetailsResponse> serieDetailsResponseCall = RetrofitManager.getInstance().service().getSerieDetails(serieID);
        serieDetailsResponseCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<SerieDetailsResponse> call, Response<SerieDetailsResponse> response) {
        SerieDetailsResponse serieDetailsResponse = response.body();
        if(response.isSuccessful() && serieDetailsResponse!=null){

            serieDetails = serieDetailsResponse.getTvShow();

            serieName.setText(serieDetails.getName());
            serieStatus.setText(serieDetails.getStatus());
            serieRuntime.setText(serieDetails.getRuntime() + "min");
            serieNetwork.setText(serieDetails.getNetwork());
            serieRate.setText(String.format("%.1f",Double.parseDouble(serieDetails.getRating())));
            serieGenre.setText(String.join(", ", serieDetails.getGenres()));
            full_description = serieDetails.getDescription();
            if(serieDetails.getDescription().length() > 150){
                serieDescription.setText(serieDetails.getDescription().substring(0, 150) + "...");
            }
            else {
                serieDescription.setText(serieDetails.getDescription());
            }
            Picasso.get().load(serieDetails.getImagePath()).into(serieImage);

            Gson gson = new Gson();
            String json = gson.toJson(serieDetails);
            SaveData("serie_episodes", json);

            // Firebase
            LoadSeriesFirebase();
        }
    }

    @Override
    public void onFailure(Call<SerieDetailsResponse> call, Throwable t) {
        Toast.makeText(this, "Pogreška pri dohvaćanju! Provjerite da li ste spojeni na internet.", Toast.LENGTH_LONG).show();
    }

    private void LoadSeriesFirebase(){
        FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        listKeys.add(Integer.valueOf(snapshot.getKey()));

                        if(snapshot.getValue(UserSerie.class).getSerieID() == serieDetails.getId()){
                            isAlreadyInList = true;
                            Key = Integer.parseInt(snapshot.getKey());
                            serieFromDatabase = snapshot.getValue(UserSerie.class);

                            Gson gson = new Gson();
                            String json = gson.toJson(serieFromDatabase);
                            SaveData("serie_db_episodes", json);
                            break;
                        }
                    }
                }

                if (serieFromDatabase.getYoutubeLink() != null) {
                    youtubeLayout.setVisibility(View.VISIBLE);
                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            String videoId = serieFromDatabase.getYoutubeLink();
                            youTubePlayer.loadVideo(videoId, 0);
                            youTubePlayer.pause();
                        }
                    });
                    youTubePlayerView.setVisibility(View.VISIBLE);
                }
                else {
                    youTubePlayerView.setVisibility(View.GONE);
                }

                if(!isAlreadyInList) {
                    if (listKeys.size() > 0){
                        NextKey = listKeys.get(listKeys.size() - 1) + 1;
                    }
                    else {
                        NextKey = 0;
                    }
                    ChangeToAdd();

                    numberOfSeasons = serieDetails.getEpisodes().get(serieDetails.getEpisodes().size() - 1).getSeason();
                    for (int i = 1; i <= numberOfSeasons; i++) {
                        seasonList.add("Season "+ i);
                    }

                    initializeRecyclerViewNotAdded(seasonList);
                }
                else {
                    ChangeToRemove();

                    for (SerieSeason season : serieFromDatabase.getSeasons()){
                        SeasonList.add(season);
                    }

                    initializeRecyclerView(SeasonList, true);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SerieDetail.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ChangeToAdd() {
        add_serie.setImageResource(R.drawable.add);
        add_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSerie();
                Key = NextKey;
                ChangeToRemove();
            }
        });
    }
    private void AddSerie () {
        List<SerieSeason> seasons = new ArrayList<>();
        List<SerieEpisodes> episodes = new ArrayList<>();
        int currentSeason = 1;
        int currentSeasonHelper = 0;
        int numOfEpisodes = 0;
        SerieEpisodes lastEpisode = serieDetails.getEpisodes().get(serieDetails.getEpisodes().size() - 1);

        for (SerieEpisodes episode : serieDetails.getEpisodes()) {
            currentSeasonHelper = episode.getSeason();
            if(currentSeason == currentSeasonHelper) {
                numOfEpisodes++;
                episodes.add(episode);
            }
            else {
                SerieSeason season = new SerieSeason("Season " +  currentSeason, false, numOfEpisodes, episodes);
                seasons.add(season);
                episodes = new ArrayList<>();
                episode.setWatched(false);
                episodes.add(episode);
                numOfEpisodes = 1;
                currentSeason++;
            }
            // ako je zadnja epizoda serije
            if(lastEpisode.getSeason() == episode.getSeason() && lastEpisode.getEpisode() == episode.getEpisode()) {
                SerieSeason season = new SerieSeason("Season " +  currentSeason, false, numOfEpisodes, episodes);
                seasons.add(season);
            }
        }

        DatabaseReference serije = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije");
        UserSerie serie = new UserSerie(serieDetails.getId(), "S01E01", 0, serieDetails.getEpisodes().size(), serieDetails.getName(), serieDetails.getPermalink(), serieDetails.getUrl(), serieDetails.getDescription(), serieDetails.getDescriptionSource(), serieDetails.getStartDate(), serieDetails.getEndDate(), serieDetails.getCountry(), serieDetails.getStatus(), serieDetails.getRuntime(), serieDetails.getNetwork(), serieDetails.getYoutubeLink(), serieDetails.getImagePath(), serieDetails.getThumbnail(), serieDetails.getRating(), serieDetails.getRatingCount(), serieDetails.getGenres(), seasons);
        serije.child(String.valueOf(NextKey)).setValue(serie);


        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void ChangeToRemove() {
        add_serie.setImageResource(R.drawable.remove);
        add_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(SerieDetail.this);
                myAlertDialog.setTitle("Potvrda");
                myAlertDialog.setMessage("Jeste li sigurni da želite ukloniti ovu seriju sa svoje liste? Sve vaše označene sezone i epizode će također biti uklonjene..");
                myAlertDialog.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RemoveSerie();
                        NextKey = Key;
                        ChangeToAdd();
                } });
                myAlertDialog.setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                } });
                myAlertDialog.show();
            }
        });
    }
    private void RemoveSerie () {
        DatabaseReference serije = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije");
        serije.child(String.valueOf(Key)).removeValue();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void SaveData (String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void initializeRecyclerView(List<SerieSeason> seasons, boolean isInDatabase){
        seasonsAdapter = new SeasonsAdapter(seasons, mClick, isInDatabase);
        seasonsRecyclerView.setAdapter(seasonsAdapter);
    }

    OnSeasonClick mClick = new OnSeasonClick() {
        @Override
        public void onClick(SerieSeason season) {
            Intent intent = new Intent(SerieDetail.this, Seasons.class );
            intent.putExtra(SEASON, SeasonList.indexOf(season) + 1);
            intent.putExtra(SERIEKEY, Key);
            startActivityForResult(intent, REQUEST);
        }

        @Override
        public void onSeasonCheck(int position) {
            // Toast.makeText(SerieDetail.this, "Pozicija je: " + position, Toast.LENGTH_SHORT).show();
            DatabaseReference season = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key + "/seasons/" + position);
            season.child("watched").setValue(!SeasonList.get(position).isWatched());
            SeasonList.get(position).setWatched(!SeasonList.get(position).isWatched());
            seasonsAdapter.notifyItemChanged(position);
            // Update za svaku epizodu te serije
            for (int i = 0; i < serieFromDatabase.getSeasons().get(position).getEpisodes().size(); i++) {
                DatabaseReference episodeDb = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key + "/seasons/" + position + "/episodes/" + i);
                episodeDb.child("watched").setValue(SeasonList.get(position).isWatched());
                serieFromDatabase.getSeasons().get(position).getEpisodes().get(i).setWatched(SeasonList.get(position).isWatched());
            }

            //Update -> episodesWatched; episodesLeft; nextEpisode;
            DatabaseReference updateInfoDb = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key);
            if(SeasonList.get(position).isWatched()) {
                updateInfoDb.child("episodesWatched").setValue(GetEpisodesWatched());
                serieFromDatabase.setEpisodesWatched(GetEpisodesWatched());
                updateInfoDb.child("episodesLeft").setValue(GetEpisodesLeft());
                serieFromDatabase.setEpisodesLeft(GetEpisodesLeft());
            }
            else {
                updateInfoDb.child("episodesWatched").setValue(GetEpisodesWatched());
                serieFromDatabase.setEpisodesWatched(GetEpisodesWatched());
                updateInfoDb.child("episodesLeft").setValue(GetEpisodesLeft());
                serieFromDatabase.setEpisodesLeft(GetEpisodesLeft());
            }
            updateInfoDb.child("nextEpisode").setValue(GetNextEpisode());
            serieFromDatabase.setNextEpisode(GetNextEpisode());

            Gson gson = new Gson();
            String json = gson.toJson(serieFromDatabase);
            SaveData("serie_db_episodes", json);
        }
    };

    private int GetEpisodesWatched () {
        int EpisodesWatchedDb = 0;
        for(SerieSeason seasonHelper : serieFromDatabase.getSeasons() ) {
            for (SerieEpisodes episodeHelper : seasonHelper.getEpisodes()){
                if(episodeHelper.isWatched()){
                    EpisodesWatchedDb++;
                }
            }
        }
        return EpisodesWatchedDb;
    }

    private int GetEpisodesLeft () {
        int EpisodesLeftDb = 0;
        for(SerieSeason seasonHelper : serieFromDatabase.getSeasons() ) {
            for (SerieEpisodes episodeHelper : seasonHelper.getEpisodes()){
                if(!episodeHelper.isWatched()){
                    EpisodesLeftDb++;
                }
            }
        }
        return EpisodesLeftDb;
    }

    private String GetNextEpisode() {
        String nextEpisodeDb = "";
        for(SerieSeason seasonHelper : serieFromDatabase.getSeasons() ) {
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
        if(nextEpisodeDb.isEmpty()) {
            nextEpisodeDb = " -/- ";
        }
        return nextEpisodeDb;
    }

    private void initializeRecyclerViewNotAdded(List<String> seasons){
        seasonsNotAddedAdapter = new SeasonsAdapterNotAdded(seasons, mClickNot);
        seasonsRecyclerView.setAdapter(seasonsNotAddedAdapter);
    }

    OnSeasonNotAddedClick mClickNot = new OnSeasonNotAddedClick() {
        @Override
        public void onClick(String name) {
            Intent intent = new Intent(SerieDetail.this, Seasons.class );
            intent.putExtra(SEASON, seasonList.indexOf(name) + 1);
            startActivity(intent);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST) {
            if (data.hasExtra("updatedSerieDetails") && data.hasExtra("whichSeason")) {
                Bundle extras = data.getExtras();

                Gson gson = new Gson();
                UserSerie serie = new UserSerie();

                String json = data.getStringExtra("updatedSerieDetails");
                if(json != null) {
                    serie = gson.fromJson(json, UserSerie.class);
                }
                int whichSeason = extras.getInt("whichSeason");

                SeasonList.get(whichSeason).setWatched(serie.getSeasons().get(whichSeason).isWatched());
                serieFromDatabase = serie;
                String json2 = gson.toJson(serieFromDatabase);
                SaveData("serie_db_episodes", json);
                seasonsAdapter.notifyItemChanged(whichSeason);

            }
        }
    }
}