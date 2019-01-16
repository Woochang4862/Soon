package com.lusle.soon;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.lusle.soon.Adapter.TempMainViewPagerAdapter;
import com.lusle.soon.Fragment.CompanyFragment;
import com.lusle.soon.Fragment.DateFragment;
import com.lusle.soon.Fragment.GenreFragment;
import com.lusle.soon.ViewPagerBottomSheet.BottomSheetUtils;
import com.lusle.soon.ViewPagerBottomSheet.ViewPagerBottomSheetBehavior;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class MainActivity extends BaseActivity {

    @BindView(R.id.activity_main_detail)
    Button detail_btn;
    @BindView(R.id.activity_main_viewpager)
    ViewPager viewPager;
    @BindView(R.id.activity_main_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.activity_main_poster)
    ImageView poster;
    @BindView(R.id.activity_main_handle)
    View handle;
    private static ViewPagerBottomSheetBehavior bottomSheetBehavior;

    private static final String TAG = "MainActivity";
    private int previousState = ViewPagerBottomSheetBehavior.STATE_COLLAPSED;
    private double screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        //TODO:get Poster from API server
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500/wsVseA7i3FqX24m26Z2gD3EtH4l.jpg")
                .centerCrop()
                .fit()
                .into(poster);


        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new TempMainViewPagerAdapter(getSupportFragmentManager(), this, TempMainViewPagerAdapter.TabItem.COMPANY, TempMainViewPagerAdapter.TabItem.GENRE, TempMainViewPagerAdapter.TabItem.DATE));
        tabLayout.setupWithViewPager(viewPager);
        BottomSheetUtils.setupViewPager(viewPager);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
                CalligraphyUtils.applyFontToTextView(tab.getCustomView().findViewById(R.id.tabName), TypefaceUtils.load(getAssets(), getResources().getString(R.string.NanumBarunGothic_path)));
                tab.getCustomView().findViewById(R.id.underline).setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                CalligraphyUtils.applyFontToTextView(tab.getCustomView().findViewById(R.id.tabName), TypefaceUtils.load(getAssets(), getResources().getString(R.string.NanumBarunGothicUltraLight_path)));
                tab.getCustomView().findViewById(R.id.underline).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (bottomSheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
            }
        });
        for (int i = 0; i < tabLayout.getTabCount(); i++) { //TODO: Tab Setting not pragmatically -> XML Code
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.tab_text);
            if (tab.isSelected()) {
                CalligraphyUtils.applyFontToTextView(tab.getCustomView().findViewById(R.id.tabName), TypefaceUtils.load(getAssets(), getResources().getString(R.string.NanumBarunGothic_path)));
                tab.getCustomView().findViewById(R.id.underline).setVisibility(View.VISIBLE);
            } else {
                CalligraphyUtils.applyFontToTextView(tab.getCustomView().findViewById(R.id.tabName), TypefaceUtils.load(getAssets(), getResources().getString(R.string.NanumBarunGothicUltraLight_path)));
                tab.getCustomView().findViewById(R.id.underline).setVisibility(View.INVISIBLE);
            }
        }


        detail_btn.setOnClickListener(v -> {
            //TODO:Moving activity to Show More
        });


        bottomSheetBehavior = ViewPagerBottomSheetBehavior.from(findViewById(R.id.activity_main_main));
        bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setBottomSheetCallback(new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                if (ViewPagerBottomSheetBehavior.STATE_DRAGGING == i) {
                    if (previousState == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
                        setVisibilityOfHandle(View.VISIBLE);
                        findViewById(R.id.activity_main_handle_background).setBackgroundResource(R.drawable.activiry_main_rounded_background_white);
                    }
                } else if (ViewPagerBottomSheetBehavior.STATE_EXPANDED == i) {
                    setVisibilityOfHandle(View.INVISIBLE);
                    findViewById(R.id.activity_main_handle_background).setBackgroundResource(R.color.white);
                }

                checkingState(i);
                CompanyFragment.getNestedScrollingEnabled();
                GenreFragment.getNestedScrollingEnabled();
                DateFragment.getNestedScrollingEnabled();

                previousState = i;
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });

        //Setting Size using Observer
        tabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                final float scale = getResources().getDisplayMetrics().density;


                int posterHeight = (int) Math.round(screenHeight) - bottomSheetBehavior.getPeekHeight() / 2;
                poster.setMinimumHeight(posterHeight);
                poster.setMaxHeight(posterHeight);


                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) detail_btn.getLayoutParams();
                params.bottomMargin = Math.round((40 * scale + 0.5f) + bottomSheetBehavior.getPeekHeight());


                bottomSheetBehavior.setPeekHeight(
                        Math.round(getDimen(R.dimen.activity_main_tabLayout_height) + getDimen(R.dimen.fragment_company_handle_margin) * 2 + getDimen(R.dimen.fragment_company_handle_height))
                );
            }
        });
    }

    private void checkingState(int state) {
        switch (state) {
            case ViewPagerBottomSheetBehavior.STATE_DRAGGING:
                Log.d(TAG, "STATE_DRAGGING");
                break;
            case ViewPagerBottomSheetBehavior.STATE_SETTLING:
                Log.d(TAG, "STATE_SETTLING");
                break;
            case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
                Log.d(TAG, "STATE_EXPANDED");
                break;
            case ViewPagerBottomSheetBehavior.STATE_COLLAPSED:
                Log.d(TAG, "STATE_COLLAPSED");
                break;
            case ViewPagerBottomSheetBehavior.STATE_HIDDEN:
                Log.d(TAG, "STATE_HIDDEN");
                break;
        }
    }

    private void setVisibilityOfHandle(int visibility) {
        handle.setVisibility(visibility);
    }

    private float getDimen(int d) {
        return getResources().getDimension(d);
    }

    public static void setBottomSheetBehaviorState(int state) {
        bottomSheetBehavior.setState(state);
    }
}
