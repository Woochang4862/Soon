package com.lusle.android.soon.Model.Schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubscribedTopicsResult {
    @SerializedName("topics")
    @Expose
    private ArrayList<String> topics;

    public ArrayList<String> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<String> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "SubscribedTopicsResult{" +
                "topics=" + topics +
                '}';
    }
}
