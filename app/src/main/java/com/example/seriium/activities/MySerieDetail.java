package com.example.seriium.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seriium.R;
import com.example.seriium.adapters.SeasonsAdapter;
import com.example.seriium.listeners.OnSeasonClick;
import com.example.seriium.models.SerieDetails;
import com.example.seriium.models.SerieDetailsResponse;
import com.example.seriium.models.SerieEpisodes;
import com.example.seriium.models.SerieSeason;
import com.example.seriium.models.UserSerie;
import com.example.seriium.network.RetrofitManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.seriium.activities.Seasons.SEASON;
import static com.example.seriium.activities.Seasons.SERIEKEY;

public class MySerieDetail extends AppCompatActivity {

    // Firebase
    public FirebaseAuth mAuth;
    private UserSerie serieDetails;
    private boolean isInDatabase = true;

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

    private Button go_back;
    private FloatingActionButton add_serie;
    private ImageButton goToUser;

    // Description expansion
    private String full_description;
    private boolean expanded = false;

    // Seasons below
    private int numberOfSeasons;
    private List<SerieSeason> SeasonList = new ArrayList<>();
    public RecyclerView seasonsRecyclerView;
    private SeasonsAdapter seasonsAdapter;

    // UserSerie
    private boolean isAlreadyInList = false;
    private int Key = -1;
    private int NextKey = -1;
    private List<Integer> listKeys = new ArrayList<>();

    public static final int REQUEST = 3;
    private YouTubePlayerView youTubePlayerView;
    private LinearLayout youtubeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serie_detail);

        SaveData("serie_db_episodes", null);
        SaveData("isStillAdded", "default");

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

        LoadSerieFirebase();
    }

    private void LoadSerieFirebase(){
        FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        listKeys.add(Integer.valueOf(snapshot.getKey()));

                        if(snapshot.getValue(UserSerie.class).getSerieID() == serieID){
                            isAlreadyInList = true;
                            Key = Integer.parseInt(snapshot.getKey());
                            serieDetails = snapshot.getValue(UserSerie.class);

                            Gson gson = new Gson();
                            String json = gson.toJson(serieDetails);
                            SaveData("serie_db_episodes", json);
                            break;
                        }
                    }
                }

                if (serieDetails.getYoutubeLink() != null) {
                    youtubeLayout.setVisibility(View.VISIBLE);
                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            String videoId = serieDetails.getYoutubeLink();
                            youTubePlayer.loadVideo(videoId, 0);
                            youTubePlayer.pause();
                        }
                    });
                    youTubePlayerView.setVisibility(View.VISIBLE);
                }
                else {
                    youTubePlayerView.setVisibility(View.GONE);
                }

                serieName.setText(serieDetails.getName());
                serieStatus.setText(serieDetails.getStatus());
                serieRuntime.setText(serieDetails.getRuntime() + "min");
                serieNetwork.setText(serieDetails.getNetwork());
                serieRate.setText(String.format("%.1f",Double.parseDouble(serieDetails.getRating())));
                serieGenre.setText(String.join(", ", serieDetails.getGenres()));
                Picasso.get().load(serieDetails.getImagePath()).into(serieImage);

                full_description = serieDetails.getDescription();
                if(serieDetails.getDescription().length() > 150){
                    serieDescription.setText(serieDetails.getDescription().substring(0, 150) + "...");
                }
                else {
                    serieDescription.setText(serieDetails.getDescription());
                }

                for (SerieSeason season : serieDetails.getSeasons()){
                    SeasonList.add(season);
                }
                ChangeToRemove();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MySerieDetail.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ChangeToRemove() {
        isInDatabase = true;
        initializeRecyclerView(SeasonList);
        add_serie.setImageResource(R.drawable.remove);
        add_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(MySerieDetail.this);
                myAlertDialog.setTitle("Potvrda");
                myAlertDialog.setMessage("Jeste li sigurni da želite ukloniti ovu seriju sa svoje liste? Sve vaše označene sezone i epizode će također biti uklonjene..");
                myAlertDialog.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RemoveSerie();
                        NextKey = Key;
                        ChangeToAdd();
                        seasonsAdapter.notifyDataSetChanged();
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

        isInDatabase = true;
        seasonsAdapter.notifyDataSetChanged();

        SaveData("isStillAdded", null);
    }

    private void ChangeToAdd() {
        isInDatabase = false;
        initializeRecyclerView(SeasonList);
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
        DatabaseReference series = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije");
        series.child(String.valueOf(NextKey)).setValue(serieDetails);

        SaveData("isStillAdded", "yes");
    }

    private void SaveData (String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void initializeRecyclerView(List<SerieSeason> seasons){
        seasonsAdapter = new SeasonsAdapter(seasons, mClick, isInDatabase);
        seasonsRecyclerView.setAdapter(seasonsAdapter);
    }

    OnSeasonClick mClick = new OnSeasonClick() {
        @Override
        public void onClick(SerieSeason season) {
            Intent intent = new Intent(MySerieDetail.this, Seasons.class );
            intent.putExtra(SEASON, SeasonList.indexOf(season) + 1);
            intent.putExtra(SERIEKEY, Key);
            startActivityForResult(intent, REQUEST);
        }

        @Override
        public void onSeasonCheck(int position) {
            // Toast.makeText(MySerieDetail.this, "Pozicija je: " + position, Toast.LENGTH_SHORT).show();
            DatabaseReference season = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key + "/seasons/" + position);
            season.child("watched").setValue(!SeasonList.get(position).isWatched());
            SeasonList.get(position).setWatched(!SeasonList.get(position).isWatched());
            seasonsAdapter.notifyItemChanged(position);
            // Update za svaku epizodu te serije
            for (int i = 0; i < serieDetails.getSeasons().get(position).getEpisodes().size(); i++) {
                DatabaseReference episodeDb = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key + "/seasons/" + position + "/episodes/" + i);
                episodeDb.child("watched").setValue(SeasonList.get(position).isWatched());
                serieDetails.getSeasons().get(position).getEpisodes().get(i).setWatched(SeasonList.get(position).isWatched());
            }

            //Update -> episodesWatched; episodesLeft; nextEpisode;
            DatabaseReference updateInfoDb = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key);
            if(SeasonList.get(position).isWatched()) {
                updateInfoDb.child("episodesWatched").setValue(GetEpisodesWatched());
                serieDetails.setEpisodesWatched(GetEpisodesWatched());
                updateInfoDb.child("episodesLeft").setValue(GetEpisodesLeft());
                serieDetails.setEpisodesLeft(GetEpisodesLeft());
            }
            else {
                updateInfoDb.child("episodesWatched").setValue(GetEpisodesWatched());
                serieDetails.setEpisodesWatched(GetEpisodesWatched());
                updateInfoDb.child("episodesLeft").setValue(GetEpisodesLeft());
                serieDetails.setEpisodesLeft(GetEpisodesLeft());
            }
            updateInfoDb.child("nextEpisode").setValue(GetNextEpisode());
            serieDetails.setNextEpisode(GetNextEpisode());

            Gson gson = new Gson();
            String json = gson.toJson(serieDetails);
            SaveData("serie_db_episodes", json);
        }
    };

    private int GetEpisodesWatched () {
        int EpisodesWatchedDb = 0;
        for(SerieSeason seasonHelper : serieDetails.getSeasons() ) {
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
        for(SerieSeason seasonHelper : serieDetails.getSeasons() ) {
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
        for(SerieSeason seasonHelper : serieDetails.getSeasons() ) {
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
                serieDetails = serie;
                String json2 = gson.toJson(serieDetails);
                SaveData("serie_db_episodes", json);
                seasonsAdapter.notifyItemChanged(whichSeason);

            }
        }
    }
}