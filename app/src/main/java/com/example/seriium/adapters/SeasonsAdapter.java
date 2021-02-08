package com.example.seriium.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seriium.R;
import com.example.seriium.listeners.OnSeasonClick;
import com.example.seriium.listeners.OnSerieClick;
import com.example.seriium.models.Serie;
import com.example.seriium.models.SerieEpisodes;
import com.example.seriium.models.SerieSeason;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeasonsAdapter extends RecyclerView.Adapter<SeasonsAdapter.SeasonViewHolder> {

    private List<SerieSeason> seasons_list;
    private OnSeasonClick mClick;
    private boolean isInDatabase;


    public SeasonsAdapter(List<SerieSeason> episodes, OnSeasonClick mClick, boolean isInDatabase) {
        this.seasons_list = episodes;
        this.mClick = mClick;
        this.isInDatabase = isInDatabase;
    }

    @NonNull
    @Override
    public SeasonsAdapter.SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.season, parent,false);
        return new SeasonsAdapter.SeasonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder holder, int position) {
        holder.bind(seasons_list.get(position));
    }

    @Override
    public int getItemCount() {
        return seasons_list.size();
    }


    class SeasonViewHolder extends RecyclerView.ViewHolder{
        TextView season_name;
        SerieSeason serieSeason;
        FloatingActionButton checkSeason;

        public SeasonViewHolder(View itemView) {
            super(itemView);
            season_name = itemView.findViewById(R.id.season_name);
            checkSeason = itemView.findViewById(R.id.checkSeason);

            if(isInDatabase){
                checkSeason.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mClick.onClick(serieSeason);
                }
            });

            checkSeason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClick.onSeasonCheck(getAdapterPosition());
                }
            });

        }

        public void bind(SerieSeason season){
            this.serieSeason = season;
            season_name.setText(season.getName());

            if (season.isWatched()) {
                checkSeason.setImageResource(R.drawable.check);
            }else {
                checkSeason.setImageResource(0);
            }
        }
    }

}
