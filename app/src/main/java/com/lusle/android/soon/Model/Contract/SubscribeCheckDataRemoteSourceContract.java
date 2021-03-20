package com.lusle.android.soon.Model.Contract;

import java.util.ArrayList;

public interface SubscribeCheckDataRemoteSourceContract {
    interface Model {

        interface OnFinishedListener {
            void onFinished(ArrayList<String> topics);

            void onFailure(Throwable t);
        }

        void setOnFinishedListener(OnFinishedListener onFinishedListener);

        void checkSubscribe(String token, String topic);

        void checkSubscribedTopics(String token);

    }
}
