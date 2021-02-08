package com.example.seriium.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seriium.R;
import com.example.seriium.listeners.OnSeasonClick;
import com.example.seriium.listeners.OnSeasonNotAddedClick;
import com.example.seriium.models.SerieSeason;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SeasonsAdapterNotAdded extends RecyclerView.Adapter<SeasonsAdapterNotAdded.SeasonNotAddedViewHolder>  {

    private List<String> seasons_list;
    private OnSeasonNotAddedClick mClick;

    public SeasonsAdapterNotAdded(List<String> episodes, OnSeasonNotAddedClick mClick) {
        this.seasons_list = episodes;
        this.mClick = mClick;
    }

    @NonNull
    @Override
    public SeasonsAdapterNotAdded.SeasonNotAddedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.season, parent,false);
        return new SeasonsAdapterNotAdded.SeasonNotAddedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonNotAddedViewHolder holder, int position) {
        holder.bind(seasons_list.get(position));
    }

    @Override
    public int getItemCount() {
        return seasons_list.size();
    }

    class SeasonNotAddedViewHolder extends RecyclerView.ViewHolder{
        TextView season_name;
        String seasonName;

        public SeasonNotAddedViewHolder(View itemView) {
            super(itemView);
            season_name = itemView.findViewById(R.id.season_name);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mClick.onClick(seasonName);
                }
            });
        }

        public void bind(String season){
            this.seasonName = season;
            season_name.setText(season);
        }
    }
}
