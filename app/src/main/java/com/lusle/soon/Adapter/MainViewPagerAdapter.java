package com.lusle.soon.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lusle.soon.Fragment.CompanyFragment;
import com.lusle.soon.Fragment.DateFragment;
import com.lusle.soon.Fragment.GenreFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new CompanyFragment().newInstance();
            case 1:
                return new GenreFragment().newInstance();
            case 2:
                return new DateFragment().newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }


}
