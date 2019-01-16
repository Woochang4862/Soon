package com.lusle.soon.Adapter;

import android.content.Context;

import com.lusle.soon.Fragment.CompanyFragment;
import com.lusle.soon.Fragment.DateFragment;
import com.lusle.soon.Fragment.GenreFragment;
import com.lusle.soon.R;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TempMainViewPagerAdapter extends FragmentPagerAdapter {

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

    public TempMainViewPagerAdapter(FragmentManager fragmentManager, Context context, TabItem... tabItems) {
        super(fragmentManager);
        this.context = context;
        this.tabItems = tabItems;
    }

    @Override
    public Fragment getItem(int position) {
        return newInstance(tabItems[position].fragmentClass);
    }

    private Fragment newInstance(Class<? extends Fragment> fragmentClass) {
        try {
            return fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
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
