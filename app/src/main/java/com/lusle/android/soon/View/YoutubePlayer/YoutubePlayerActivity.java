package com.lusle.android.soon.View.YoutubePlayer;

import android.os.Bundle;

import com.lusle.android.soon.R;
import com.lusle.android.soon.View.BaseActivity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import androidx.annotation.NonNull;

public class YoutubePlayerActivity extends BaseActivity {

    private String VIDEO_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        VIDEO_ID = getIntent().getStringExtra("VIDEO_ID");

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.enterFullScreen();

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(VIDEO_ID, 0);
                youTubePlayer.play();
            }
        });
    }
}
