package com.lusle.android.soon.view.youtube_player;

import android.os.Bundle;
import android.view.View;

import com.lusle.android.soon.R;
import com.lusle.android.soon.view.BaseActivity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import androidx.annotation.NonNull;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class YoutubePlayerActivity extends BaseActivity implements YouTubePlayerListener {

    private String VIDEO_ID;
    private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        VIDEO_ID = getIntent().getStringExtra("VIDEO_ID");

        youTubePlayerView = findViewById(R.id.youtube_player);
        getLifecycle().addObserver(youTubePlayerView);

        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                //.fullscreen(1)
                .build();

        youTubePlayerView.initialize(this, iFramePlayerOptions);

        /*youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {

            }

            @Override
            public void onExitFullscreen() {

            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }

    @Override
    public void onApiChange(@NonNull YouTubePlayer youTubePlayer) {

    }

    @Override
    public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float v) {

    }

    @Override
    public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError playerError) {

    }

    @Override
    public void onPlaybackQualityChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackQuality playbackQuality) {

    }

    @Override
    public void onPlaybackRateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackRate playbackRate) {

    }

    @Override
    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
        youTubePlayer.loadVideo(VIDEO_ID, 0);
        youTubePlayer.play();
    }

    @Override
    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState playerState) {

    }

    @Override
    public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float v) {

    }

    @Override
    public void onVideoId(@NonNull YouTubePlayer youTubePlayer, @NonNull String s) {

    }

    @Override
    public void onVideoLoadedFraction(@NonNull YouTubePlayer youTubePlayer, float v) {

    }
}
