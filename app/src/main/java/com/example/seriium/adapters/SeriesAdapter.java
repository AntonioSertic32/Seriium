package com.example.seriium.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seriium.R;
import com.example.seriium.listeners.OnSerieClick;
import com.example.seriium.models.Serie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SerieViewHolder> {

    private List<Serie> serie;
    private OnSerieClick mClick;

    public SeriesAdapter(List<Serie> serie, OnSerieClick mClick) {
        this.serie = serie;
        this.mClick = mClick;
    }

    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serie, parent,false);
        return new SerieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder holder, int position) {
        holder.bind(serie.get(position));
    }

    @Override
    public int getItemCount() {
        return serie.size();
    }


    class SerieViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView start_date;
        TextView country;
        TextView network;
        TextView status;
        ImageView image_thumbnail_path;
        Serie serie;

        public SerieViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            start_date = itemView.findViewById(R.id.textStarted);
            country = itemView.findViewById(R.id.textCountry);
            network = itemView.findViewById(R.id.textNetwork);
            status = itemView.findViewById(R.id.textStatus);
            image_thumbnail_path = itemView.findViewById(R.id.item_serie_poster);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mClick.onClick(serie);
                }
            });
        }

        public void bind(Serie serie){
            this.serie = serie;
            name.setText(serie.getName());
            start_date.setText(serie.getStartDate());
            country.setText(serie.getCountry());
            network.setText(serie.getNetwork());
            status.setText(serie.getStatus());

            Picasso.get().load(serie.getThumbnail()).into(image_thumbnail_path);
        }
    }
}
