package com.lusle.android.soon.Model.Contract;

import com.lusle.android.soon.Model.Schema.MovieResult;

public interface MovieDataRemoteSourceContract {
    interface Model{
        void getThisMonthMovieResult(String region, int page);

        void discoverMovieWithDate(String region, String date, int page);

        interface OnFinishedListener {
            void onFinished(MovieResult movieArrayList);

            void onFailure(Throwable t);
        }

        void setOnFinishedListener(OnFinishedListener onFinishedListener);
    }
}
