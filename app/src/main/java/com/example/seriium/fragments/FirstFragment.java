package com.example.seriium.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.example.seriium.listeners.OnMySerieClick;
import com.example.seriium.models.SerieDetails;
import com.example.seriium.models.UserSerie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.seriium.activities.SerieDetail.REQUEST;
import static com.example.seriium.activities.SerieDetail.SERIE_ID;

public class FirstFragment extends Fragment {

    // Firebase
    public FirebaseAuth mAuth;

    // Layout
    public RecyclerView seriesRecyclerView;
    private MySeriesAdapter myseriesAdapter;
    private ProgressBar progressBar;
    private TextView emptyListText;

    // UserSerie
    private List<UserSerie> databaseSeries = new ArrayList<>();

    // SerieDetails
    public static final int MY_DETAILS_REQUEST = 1;

    public FirstFragment() {
    }

    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first,container,false);

        seriesRecyclerView = view.findViewById(R.id.my_serie_list);
        seriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = view.findViewById(R.id.f1_progressbar);
        emptyListText = view.findViewById(R.id.emptyListText);

        mAuth = FirebaseAuth.getInstance();

        LoadSeriesFirebase();

        return view;
    }

    private void LoadSeriesFirebase(){
        FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/serije" ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    databaseSeries.add(snapshot.getValue(UserSerie.class));
                }
                progressBar.setVisibility(View.GONE);
                if(databaseSeries.isEmpty()) {
                    emptyListText.setVisibility(View.VISIBLE);
                }
                initializeRecyclerView();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    void initializeRecyclerView(){
        myseriesAdapter = new MySeriesAdapter(databaseSeries, mClick);
        seriesRecyclerView.setAdapter(myseriesAdapter);
    }

    OnMySerieClick mClick = new OnMySerieClick() {
        @Override
        public void onClick(UserSerie serie) {
            Intent intent = new Intent(getActivity(), MySerieDetail.class );
            intent.putExtra(SERIE_ID, serie.getSerieID());
            startActivityForResult(intent, MY_DETAILS_REQUEST);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK && requestCode == MY_DETAILS_REQUEST) {
            if (data.hasExtra("openUser")) {

                // Make transaction
                FourthFragment nextFrag= new FourthFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            databaseSeries.clear();
            // Reload current fragment
            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }

    }
}