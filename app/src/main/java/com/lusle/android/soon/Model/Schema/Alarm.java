package com.lusle.android.soon.Model.Schema;

import android.util.Log;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Alarm implements Serializable {
    private Movie movie;
    private long milliseconds;
    private int pendingIntentID;
    private boolean active;

    public Alarm(Movie movie, long milliseconds, int pendingIntentID, boolean active) {
        this.movie = movie;
        this.milliseconds = milliseconds;
        this.pendingIntentID = pendingIntentID;
        this.active = active;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public int getPendingIntentID() {
        return pendingIntentID;
    }

    public void setPendingIntentID(int pendingIntentID) {
        this.pendingIntentID = pendingIntentID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean isT = false;
        Alarm am = (Alarm) obj;
        Log.d("####", "equals: " + pendingIntentID + ", " + am.getPendingIntentID());
        if (pendingIntentID == am.getPendingIntentID()) {
            isT = true;
        }
        return isT;
    }

    @Override
    public int hashCode() {
        return pendingIntentID;
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(pendingIntentID);
    }
}
