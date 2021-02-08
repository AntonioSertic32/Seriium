package com.example.seriium.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seriium.R;
import com.example.seriium.listeners.OnMySerieClick;
import com.example.seriium.models.SerieSeason;
import com.example.seriium.models.UserSerie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MySeriesAdapter extends RecyclerView.Adapter<MySeriesAdapter.MySerieViewHolder> {

    private List<UserSerie> series;
    private OnMySerieClick mClick;

    private int numOfEpisodes;

    public MySeriesAdapter(List<UserSerie> series, OnMySerieClick mClick) {
        this.series = series;
        this.mClick = mClick;
    }

    @NonNull
    @Override
    public MySerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_serie, parent,false);
        return new MySeriesAdapter.MySerieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySerieViewHolder holder, int position) {
        holder.bind(series.get(position));
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    class MySerieViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView next_episode;
        TextView episodes_left;
        TextView watched_episodes;
        ImageView image_thumbnail_path;
        UserSerie serie;
        ProgressBar progressBar;

        public MySerieViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mytextName);
            next_episode = itemView.findViewById(R.id.mytextNextEpisode);
            episodes_left = itemView.findViewById(R.id.mytextLeftEpisodes);
            watched_episodes = itemView.findViewById(R.id.watchedEpisodes);
            image_thumbnail_path = itemView.findViewById(R.id.item_my_serie_poster);
            progressBar = itemView.findViewById(R.id.progressBar);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mClick.onClick(serie);
                }
            });
        }

        public void bind(UserSerie serie){
            int numOfEpisodes = 0;

            for (SerieSeason season : serie.getSeasons()){
                numOfEpisodes += season.getEpisodes().size();
            }

            this.serie = serie;
            name.setText(String.valueOf(serie.getName()));
            next_episode.setText(serie.getNextEpisode());
            episodes_left.setText(String.valueOf(serie.getEpisodesLeft()));
            progressBar.setProgress((serie.getEpisodesWatched() * 100) / numOfEpisodes);
            watched_episodes.setText(serie.getEpisodesWatched() + " / " + numOfEpisodes);
            Picasso.get().load(serie.getThumbnail()).into(image_thumbnail_path);
        }
    }
}
