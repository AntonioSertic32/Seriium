package com.example.seriium.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seriium.R;
import com.example.seriium.activities.MySerieDetail;
import com.example.seriium.adapters.MySeriesAdapter;
import com.example.seriium.models.SerieEpisodes;
import com.example.seriium.models.SerieSeason;
import com.example.seriium.models.UserSerie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdFragment extends Fragment {

    // Firebase
    public FirebaseAuth mAuth;

    // UserSerie
    private List<UserSerie> databaseSeries = new ArrayList<>();

    // Layout
    private ProgressBar progressBar;
    private TextView notEnoughInformation;
    private CardView card1;
    private CardView card2;
    private CardView card3;

    private TextView statMin;
    private TextView statYearsTxt;
    private TextView statMonthsTxt;
    private TextView statDaysTxt;
    private TextView statHoursTxt;

    private TextView statEp;
    private TextView statShowsTxt;
    private TextView statSeasonesTxt;

    private TextView firstGenre;
    private TextView secondGenre;
    private List<String> listOfGenres = new ArrayList<>();

    private int minutes = 0;

    private int countEp = 0;
    private int countShows = 0;
    private int countSeasones = 0;


    public ThirdFragment() {
    }

    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third,container,false);
        progressBar = view.findViewById(R.id.f3_progressbar);
        notEnoughInformation = view.findViewById(R.id.notEnoughInformation);
        card1 = view.findViewById(R.id.card1);
        card2 = view.findViewById(R.id.card2);
        card3 = view.findViewById(R.id.card3);

        statMin = view.findViewById(R.id.statMin);
        statYearsTxt = view.findViewById(R.id.statYearsTxt);
        statMonthsTxt = view.findViewById(R.id.statMonthsTxt);
        statDaysTxt = view.findViewById(R.id.statDaysTxt);
        statHoursTxt = view.findViewById(R.id.statHoursTxt);

        statEp = view.findViewById(R.id.statEp);
        statShowsTxt = view.findViewById(R.id.statShowsTxt);
        statSeasonesTxt = view.findViewById(R.id.statSeasonesTxt);

        firstGenre = view.findViewById(R.id.firstGenre);
        secondGenre = view.findViewById(R.id.secondGenre);

        mAuth = FirebaseAuth.getInstance();
        LoadSerieFirebase();
        return view;
    }

    private void LoadSerieFirebase(){
        FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    databaseSeries.add(snapshot.getValue(UserSerie.class));
                }
                progressBar.setVisibility(View.GONE);

                if(databaseSeries.isEmpty()) {
                    notEnoughInformation.setVisibility(View.VISIBLE);
                }
                else {
                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                    card3.setVisibility(View.VISIBLE);
                    printStatistic();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void printStatistic () {

        boolean isEpisodeWatched = false;

        for(UserSerie serie : databaseSeries) {
            minutes += serie.getEpisodesWatched() * Integer.parseInt(serie.getRuntime());
            countShows++;

            for(String genreDb : serie.getGenres()){
                listOfGenres.add(genreDb);
            }

            for(SerieSeason season : serie.getSeasons()) {
                boolean isAllWatched = true;

                for(SerieEpisodes episode : season.getEpisodes()){
                    if(!episode.isWatched()) {
                        isAllWatched = false;
                    }
                    else {
                        countEp++;
                    }
                }
                if(isAllWatched){
                    countSeasones++;
                }
            }
        }

        // 1. Kartica
        statMin.setText(String.valueOf(minutes));
        Float godina = minutes / 518400f;
        Float mjesec = godina * 12f;
        Float dana = mjesec * 30f;
        Float sati = dana * 24f;
        statHoursTxt.setText("I " + String.valueOf(Math.round(sati) - (Math.round(dana) * 24)) + " sata, ali tko još broji..");
        statDaysTxt.setText(String.valueOf( Math.round(dana) ));
        statMonthsTxt.setText(String.valueOf(Math.round(mjesec)));
        statYearsTxt.setText(String.valueOf(Math.round(godina)));

        // 2. Kartica
        firstGenre.setText(mostCommon(listOfGenres));
        String mostPopularGenre = mostCommon(listOfGenres);
        listOfGenres.clear();
        for(UserSerie serie : databaseSeries) {
            for(String genreDb : serie.getGenres()){
                if(!genreDb.equals(mostPopularGenre)) {
                    listOfGenres.add(genreDb);
                }
            }
        }
        secondGenre.setText(mostCommon(listOfGenres));

        // 3. Kartica
        statEp.setText(String.valueOf(countEp));
        statShowsTxt.setText(String.valueOf(countShows));
        statSeasonesTxt.setText(String.valueOf(countSeasones));
    }

    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }
}