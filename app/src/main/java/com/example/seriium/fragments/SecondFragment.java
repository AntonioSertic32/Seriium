package com.example.seriium.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seriium.R;
import com.example.seriium.activities.SerieDetail;
import com.example.seriium.adapters.SeriesAdapter;
import com.example.seriium.listeners.OnSerieClick;
import com.example.seriium.models.Serie;
import com.example.seriium.models.SerieResponse;
import com.example.seriium.network.RetrofitManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.seriium.activities.SerieDetail.SERIE_ID;

public class SecondFragment extends Fragment implements Callback<SerieResponse> {

    public RecyclerView seriesRecyclerView;
    private SeriesAdapter seriesAdapter;
    private int page;
    private Button nextPage;
    private Button prevPage;
    private TextView paginationDisplay;
    private NestedScrollView mainScrollView;
    private ProgressBar progressBar;
    private ConstraintLayout pagination;
    private EditText search;
    private boolean popularOrSearch = false;

    public SecondFragment() {
    }

    public static SecondFragment newInstance() {
        SecondFragment fragment = new SecondFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = 1;
        Call<SerieResponse> seriesResponseCall = RetrofitManager.getInstance().service().getMostPopular(page);
        seriesResponseCall.enqueue(SecondFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second,container,false);
        seriesRecyclerView = view.findViewById(R.id.serie_list);
        seriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        search = view.findViewById(R.id.searchBar);
        search.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    callSearch(search.getText().toString());
                    hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        paginationDisplay = view.findViewById(R.id.paginationDisplay);
        mainScrollView = view.findViewById(R.id.scrollView);

        nextPage = view.findViewById(R.id.next);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadNext();
                mainScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        progressBar = view.findViewById(R.id.f2_progressbar);
        pagination = view.findViewById(R.id.paginationContainer);

        prevPage = view.findViewById(R.id.prev);
        prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPrev();
                mainScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        return view;
    }

    @Override
    public void onResponse(Call<SerieResponse> call, Response<SerieResponse> response) {
        SerieResponse serieResponse = response.body();
        progressBar.setVisibility(View.GONE);
        pagination.setVisibility(View.VISIBLE);
        if(response.isSuccessful() && serieResponse!=null){

            paginationDisplay.setText(serieResponse.getPage() + " / " + serieResponse.getTotalPages());

            if(serieResponse.getPage() == 1){
                prevPage.setEnabled(false);
                nextPage.setEnabled(true);
            }
            else if (serieResponse.getPage() == serieResponse.getTotalPages()){
                prevPage.setEnabled(true);
                nextPage.setEnabled(false);
            }
            else {
                prevPage.setEnabled(true);
                nextPage.setEnabled(true);
            }

            initializeRecyclerView(serieResponse.getTvShows());
        }
        else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Check your internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<SerieResponse> call, Throwable t) {
        Toast.makeText(getActivity(), "Check your internet connection!", Toast.LENGTH_SHORT).show();
    }

    private void callSearch (String text) {
        page = 1;
        if(!search.getText().toString().isEmpty()) {
            popularOrSearch = true;
            Call<SerieResponse> seriesResponseCall = RetrofitManager.getInstance().service().getSearch(text, page);
            seriesResponseCall.enqueue(SecondFragment.this);
        }
        else {
            popularOrSearch = false;
            Call<SerieResponse> seriesResponseCall = RetrofitManager.getInstance().service().getMostPopular(page);
            seriesResponseCall.enqueue(SecondFragment.this);
        }

    }

    void initializeRecyclerView(List<Serie> series){
        seriesAdapter = new SeriesAdapter(series, mClick);
        seriesRecyclerView.setAdapter(seriesAdapter);
    }

    public void LoadNext() {
        page++;
        if(popularOrSearch) {
            Call<SerieResponse> seriesResponseCall = RetrofitManager.getInstance().service().getSearch(search.getText().toString(), page);
            seriesResponseCall.enqueue(SecondFragment.this);
        }else {
            Call<SerieResponse> seriesResponseCall = RetrofitManager.getInstance().service().getMostPopular(page);
            seriesResponseCall.enqueue(SecondFragment.this);
        }
    }

    public void LoadPrev() {
        page--;
        if(popularOrSearch) {
            Call<SerieResponse> seriesResponseCall = RetrofitManager.getInstance().service().getSearch(search.getText().toString(), page);
            seriesResponseCall.enqueue(SecondFragment.this);
        }else {
            Call<SerieResponse> seriesResponseCall = RetrofitManager.getInstance().service().getMostPopular(page);
            seriesResponseCall.enqueue(SecondFragment.this);
        }
    }

    OnSerieClick mClick = new OnSerieClick() {
        @Override
        public void onClick(Serie serie) {

            Intent intent = new Intent(getActivity(), SerieDetail.class );
            intent.putExtra(SERIE_ID, serie.getId());
            startActivity(intent);
        }
    };

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}