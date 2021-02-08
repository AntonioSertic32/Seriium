package com.example.seriium.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.seriium.activities.Seasons.SEASON;

public class MySerieDetail extends AppCompatActivity {

    // Firebase
    public FirebaseAuth mAuth;
    private UserSerie serieDetails;
    private List<UserSerie> watchedSeasoneEisodes = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serie_detail);

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
                            break;
                        }
                    }
                }

                if(!isAlreadyInList) {
                    if (listKeys.size() > 0){
                        NextKey = listKeys.get(listKeys.size() - 1) + 1;
                    }
                    else {
                        NextKey = 0;
                    }
                    ChangeToAdd();
                }
                else {
                    ChangeToRemove();
                }

                serieName.setText(serieDetails.getName());
                serieStatus.setText(serieDetails.getStatus());
                serieRuntime.setText(serieDetails.getRuntime() + "min");
                serieNetwork.setText(serieDetails.getNetwork());
                serieRate.setText(String.format("%.1f",Double.parseDouble(serieDetails.getRating())));
                serieGenre.setText(String.join(", ", serieDetails.getGenres()));
                Picasso.get().load(serieDetails.getImagePath()).into(serieImage);

                //List<SerieSeason> serieEpisodes = serieDetails.getSeasons();

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

                initializeRecyclerView(SeasonList, true);
                checkIfSeasonWatched();

                Gson gson = new Gson();
                String json = gson.toJson(serieDetails);
                SaveData("serie_episodes", json);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MySerieDetail.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkIfSeasonWatched () { }

    private void ChangeToRemove() {
        add_serie.setImageResource(R.drawable.remove);
        add_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveSerie();
                NextKey = Key;
                ChangeToAdd();
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
        DatabaseReference series = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije");
        series.child(String.valueOf(NextKey)).setValue(serieDetails);
    }
    private void RemoveSerie () {
        DatabaseReference serije = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije");
        serije.child(String.valueOf(Key)).removeValue();
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
            Intent intent = new Intent(MySerieDetail.this, Seasons.class );
            intent.putExtra(SEASON, SeasonList.indexOf(season) + 1);
            startActivity(intent);
        }

        @Override
        public void onSeasonCheck(int position) {
            //CheckSeason(position);
            //SeasonList.set( position, "New" );
            //seasonsAdapter.notifyItemChanged(position);
        }
    };

    private void CheckSeason (int position) {
        /*
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije/" + Key + "/watchedEpisodes");
        //dohvatiti epizode te sezone
        position += 1;
        for(SerieEpisodes episode : serieDetails.getEpisodes()) {
            if(episode.getSeason() == position) {
                //Toast.makeText(MySerieDetail.this, String.valueOf(serieDetails.getEpisodes().indexOf(episode) + 1), Toast.LENGTH_SHORT).show();
                databaseRef.child(String.valueOf(serieDetails.getEpisodes().indexOf(episode))).setValue(episode);
                //watchedSeasoneEisodes.add(episode);
            }
        }
        */
        //databaseRef.child(String.valueOf(NextKey)).setValue(watchedSeasoneEisodes);
        //Toast.makeText(MySerieDetail.this, "Id serije je: " + Key, Toast.LENGTH_LONG).show();
    }

}