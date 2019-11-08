package com.lusle.android.soon.View.Main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lusle.android.soon.View.BaseActivity;
import com.lusle.android.soon.View.Search.SearchActivity;
import com.lusle.android.soon.View.Main.Company.CompanyFragment;
import com.lusle.android.soon.View.Main.Date.DateFragment;
import com.lusle.android.soon.View.Main.Genre.GenreFragment;
import com.lusle.android.soon.View.Main.Setting.SettingFragment;
import com.lusle.android.soon.View.Main.ThisMonthMovie.ThisMonthMovieFragment;
import com.lusle.android.soon.R;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton mSearchFab;
    private AdView adView;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    final private int requestCode = 666;

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //MobileAds.initialize(this, "ca-app-pub-2329923322434251~4419072683");
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM Token", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    Log.d("FCM Token", token);
                });

        adView = findViewById(R.id.adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.d(TAG, "onAdClosed: ");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d(TAG, "onAdFailedToLoad: ");
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.d(TAG, "onAdLeftApplication: ");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d(TAG, "onAdOpened: ");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG, "onAdLoaded: ");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.d(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.d(TAG, "onAdImpression: ");
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mSearchFab = findViewById(R.id.floatingActionButton);
        mSearchFab.setOnClickListener(this::presentActivity);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = ThisMonthMovieFragment.newInstance();
                    break;
                case R.id.company:
                    selectedFragment = CompanyFragment.newInstance();
                    break;
                case R.id.genre:
                    selectedFragment = GenreFragment.newInstance();
                    break;
                case R.id.date:
                    selectedFragment = DateFragment.newInstance();
                    break;
                case R.id.settings:
                    selectedFragment = SettingFragment.newInstance();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ThisMonthMovieFragment.newInstance());
        transaction.commit();

        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            DynamicToast.make(this, "종료하고 싶다면 한번 더 눌러주세요").show();
        }
    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(SearchActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivityForResult(this, intent, requestCode, options.toBundle());
    }
}