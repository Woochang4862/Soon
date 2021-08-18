package com.lusle.android.soon.View.Search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.lusle.android.soon.Adapter.SearchActivityPagerAdapter;
import com.lusle.android.soon.MySuggestionProvider;
import com.lusle.android.soon.R;
import com.lusle.android.soon.View.BaseActivity;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.viewpager.widget.ViewPager;

public class SearchActivity extends BaseActivity {

    private View rootLayout;
    private SearchView mSearchView;
    private ImageView mBackBtn, mSearchBtn;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";

    private int revealX;
    private int revealY;

    private ArrayList<OnQueryReceivedListener> queryReceivedListeners = new ArrayList<>();
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }

        rootLayout = findViewById(R.id.rootView);

        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);


            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }


        viewPager = findViewById(R.id.activity_search_viewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new SearchActivityPagerAdapter(getSupportFragmentManager(), getApplicationContext(),
                SearchActivityPagerAdapter.TabItem.ALL,
                SearchActivityPagerAdapter.TabItem.COMPANY,
                SearchActivityPagerAdapter.TabItem.MOVIE
        ));
        tabLayout = findViewById(R.id.activity_search_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (!currentQuery.equals("") && mSearchView.getQuery().toString().equals("")) {
                    mSearchView.setQuery(currentQuery, true);
                } else {
                    mSearchView.setQuery(mSearchView.getQuery(), true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = findViewById(R.id.activity_search_searchview);
        if (searchManager != null)
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryRefinementEnabled(true);
        mSearchView.requestFocus(1);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                s = s.trim();
                if (s.equals("")) {
                    DynamicToast.makeWarning(SearchActivity.this, getString(R.string.please_input_query)).show();
                } else {
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SearchActivity.this,
                            MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                    suggestions.saveRecentQuery(s, null);
                    queryReceivedListeners.get(viewPager.getCurrentItem()).onQueryReceived(s);
                    mSearchView.clearFocus();
                    currentQuery = s;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                CursorAdapter selectedView = mSearchView.getSuggestionsAdapter();
                Cursor cursor = (Cursor) selectedView.getItem(i);
                int index = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1);
                mSearchView.setQuery(cursor.getString(index), true);
                return true;
            }
        });

        mBackBtn = findViewById(R.id.activity_search_back_btn);
        mBackBtn.setOnClickListener(v -> unRevealActivity());


        mSearchBtn = findViewById(R.id.activity_search_search_btn);
        mSearchBtn.setOnClickListener(v -> mSearchView.setQuery(mSearchView.getQuery(), true));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, true);
        }
    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(1000);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    protected void unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0);

            circularReveal.setDuration(1000);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    finish();
                }
            });


            circularReveal.start();
        }
    }

    @Override
    public void onBackPressed() {
        unRevealActivity();
    }

    public interface OnQueryReceivedListener {
        void onQueryReceived(String query);
    }

    public void addQueryReceivedListener(OnQueryReceivedListener queryReceivedListener) {
        queryReceivedListeners.add(queryReceivedListener);
    }

    public ViewPager getViewPager() {
        if (null == viewPager) {
            viewPager = findViewById(R.id.activity_search_viewPager);
        }
        return viewPager;
    }
}
