package com.lusle.soon;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.soon.Adapter.MainViewPagerAdapter;
import com.lusle.soon.Fragment.CompanyFragment;
import com.lusle.soon.Fragment.DateFragment;
import com.lusle.soon.Fragment.GenreFragment;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyUtils;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static BottomSheetBehavior bottomSheetBehavior;
    private Button detail_btn;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView poster;
    private View handle;

    private int previousState = BottomSheetBehavior.STATE_COLLAPSED;
    private double screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private int[] tabName = new int[]{R.string.tab_name_1, R.string.tab_name_2, R.string.tab_name_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        poster = findViewById(R.id.activity_main_poster);
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500/wsVseA7i3FqX24m26Z2gD3EtH4l.jpg")
                .centerCrop()
                .fit()
                .into(poster);


        handle = findViewById(R.id.activity_main_handle);


        tabLayout = findViewById(R.id.activity_main_tablayout);
        viewPager = findViewById(R.id.activity_main_viewpager);
        viewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                viewPager.setCurrentItem(tab.getPosition());
                CalligraphyUtils.applyFontToTextView((TextView) tab.getCustomView().findViewById(R.id.tabName), TypefaceUtils.load(getAssets(), getResources().getString(R.string.NanumBarunGothic_path)));
                tab.getCustomView().findViewById(R.id.underline).setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                CalligraphyUtils.applyFontToTextView((TextView) tab.getCustomView().findViewById(R.id.tabName), TypefaceUtils.load(getAssets(), getResources().getString(R.string.NanumBarunGothicUltraLight_path)));
                tab.getCustomView().findViewById(R.id.underline).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.tab_text);
            TextView tempTextView = tab.getCustomView().findViewById(R.id.tabName);
            tempTextView.setText(tabName[i]);
            if (tab.isSelected()) {
                CalligraphyUtils.applyFontToTextView((TextView) tab.getCustomView().findViewById(R.id.tabName), TypefaceUtils.load(getAssets(), getResources().getString(R.string.NanumBarunGothic_path)));
                tab.getCustomView().findViewById(R.id.underline).setVisibility(View.VISIBLE);
            } else {
                CalligraphyUtils.applyFontToTextView((TextView) tab.getCustomView().findViewById(R.id.tabName), TypefaceUtils.load(getAssets(), getResources().getString(R.string.NanumBarunGothicUltraLight_path)));
                tab.getCustomView().findViewById(R.id.underline).setVisibility(View.INVISIBLE);
            }
        }


        detail_btn = findViewById(R.id.activity_main_detail);
        detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:Show more
            }
        });


        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.activity_main_main));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        final ViewTreeObserver observer = tabLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
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
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (BottomSheetBehavior.STATE_DRAGGING == i) {
                    if (previousState == BottomSheetBehavior.STATE_EXPANDED) {
                        setVisibilityOfHandle(View.VISIBLE);
                        findViewById(R.id.activity_main_handle_background).setBackgroundResource(R.drawable.activiry_main_rounded_background_white);
                    }
                } else if (BottomSheetBehavior.STATE_EXPANDED == i) {
                    setVisibilityOfHandle(View.INVISIBLE);
                    findViewById(R.id.activity_main_handle_background).setBackgroundResource(R.color.white);
                }
                if(BottomSheetBehavior.STATE_COLLAPSED == i){
                    setVisibilityOfHandle(View.VISIBLE);
                    findViewById(R.id.activity_main_handle_background).setBackgroundResource(R.drawable.activiry_main_rounded_background_white);
                }

                previousState = i;
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });


    }

    private void setVisibilityOfHandle(int visibility){
        handle.setVisibility(visibility);
    }

    private float getDimen(int d) {
        return getResources().getDimension(d);
    }

    public static void setBottomSheetBehaviorState(int state) {
        bottomSheetBehavior.setState(state);
    }
}
