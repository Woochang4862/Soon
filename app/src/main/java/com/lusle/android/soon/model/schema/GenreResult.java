package com.lusle.android.soon.model.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GenreResult {
    @SerializedName("genres")
    @Expose
    private ArrayList<Genre> genres;

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }
}
