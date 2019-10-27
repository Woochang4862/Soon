package com.lusle.android.soon.Model.Contract;

import com.lusle.android.soon.Model.Schema.MovieResult;

public interface MovieDataRemoteSourceContract {
    interface Model{
        interface OnFinishedListener {
            void onFinished(MovieResult movieArrayList);

            void onFailure(Throwable t);
        }

        void setOnFinishedListener(OnFinishedListener onFinishedListener);
        void getThisMonthMovieResult(int page);
        void discoverMovieWithDate(String date, int page);
    }
}
