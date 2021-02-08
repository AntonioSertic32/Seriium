package com.example.seriium.listeners;

import com.example.seriium.models.Serie;
import com.example.seriium.models.SerieEpisodes;
import com.example.seriium.models.SerieSeason;
import com.example.seriium.models.UserSerie;

public interface OnSeasonClick {
    void onClick(SerieSeason season);
    void onSeasonCheck(int position);
}
