package com.example.seriium.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seriium.R;
import com.example.seriium.activities.Seasons;
import com.example.seriium.listeners.OnSeasonClick;
import com.example.seriium.models.SerieEpisodes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class EpisodesAdapter extends  RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder> {

    private List<SerieEpisodes> episode_list;
    private boolean not_added;
    private OnSeasonClick mClick;

    public EpisodesAdapter(List<SerieEpisodes> episodes, boolean not_added, OnSeasonClick mClick) {
        this.episode_list = episodes;
        this.not_added = not_added;
        this.mClick = mClick;
    }

    @NonNull
    @Override
    public EpisodesAdapter.EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode, parent,false);
        return new EpisodesAdapter.EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesAdapter.EpisodeViewHolder holder, int position) {
        holder.bind(episode_list.get(position));
    }

    @Override
    public int getItemCount() {
            return episode_list.size();
    }


    class EpisodeViewHolder extends RecyclerView.ViewHolder{
        TextView episodeName;
        TextView episodeSE;
        TextView episodeAirdate;
        FloatingActionButton checkEpisode;

        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            episodeName = itemView.findViewById(R.id.episode_name);
            episodeSE = itemView.findViewById(R.id.episode_se);
            episodeAirdate = itemView.findViewById(R.id.episode_airdate);

            checkEpisode = itemView.findViewById(R.id.checkEpisode);
            if(not_added) {
                checkEpisode.setVisibility(View.VISIBLE);
            }

            checkEpisode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClick.onSeasonCheck(getAdapterPosition());
                }
            });
        }

        public void bind(SerieEpisodes episode){
            episodeName.setText(episode.getName());
            episodeSE.setText("S" + episode.getSeason() + " E" + episode.getEpisode());
            episodeAirdate.setText(episode.getAir_date());

            if (episode.isWatched()) {
                checkEpisode.setImageResource(R.drawable.check);
            }else {
                checkEpisode.setImageResource(0);
            }
        }
    }
}
