
package com.lusle.android.soon.Model.Schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Videos {

    @SerializedName("results")
    @Expose
    private ArrayList<Video> results = null;

    public ArrayList<Video> getResults() {
        return results;
    }

    public void setResults(ArrayList<Video> results) {
        this.results = results;
    }

}
