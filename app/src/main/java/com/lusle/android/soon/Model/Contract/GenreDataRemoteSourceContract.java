package com.lusle.android.soon.Model.Contract;

import com.lusle.android.soon.Model.Schema.Genre;

import java.util.ArrayList;

public interface GenreDataRemoteSourceContract {
    interface Model{
        interface OnFinishedListener {
            void onFinished(ArrayList<Genre> movieArrayList);

            void onFailure(Throwable t);
        }

        void setOnFinishedListener(OnFinishedListener onFinishedListener);
        void getGenreList();
    }
}
