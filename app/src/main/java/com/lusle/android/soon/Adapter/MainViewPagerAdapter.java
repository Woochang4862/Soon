package com.lusle.android.soon.Adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.lusle.android.soon.Fragments.CompanyFragment;
import com.lusle.android.soon.Fragments.DateFragment;
import com.lusle.android.soon.Fragments.GenreFragment;
import com.lusle.android.soon.R;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    public enum TabItem {
        COMPANY(CompanyFragment.class, R.string.tab_name_1),
        GENRE(GenreFragment.class, R.string.tab_name_2),
        DATE(DateFragment.class, R.string.tab_name_3);

        private final Class<? extends Fragment> fragmentClass;
        private final int titleResId;

        TabItem(Class<? extends Fragment> fragmentClass, @StringRes int titleResId) {
            this.fragmentClass = fragmentClass;
            this.titleResId = titleResId;
        }
    }

    private final TabItem[] tabItems;
    private final Context context;

    public MainViewPagerAdapter(FragmentManager fragmentManager, Context context, TabItem... tabItems) {
        super(fragmentManager);
        this.context = context;
        this.tabItems = tabItems;
    }

    @Override
    public Fragment getItem(int position) {
        return newInstance(tabItems[position].fragmentClass);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Fragment newInstance(Class<? extends Fragment> fragmentClass) {
        try {
            return fragmentClass.newInstance();
        } catch (@SuppressLint("NewApi") InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("fragment must have public no-arg constructor: " + fragmentClass.getName(), e);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(tabItems[position].titleResId);
    }

    @Override
    public int getCount() {
        return tabItems.length;
    }
}