package com.lusle.soon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.lusle.soon.R;
import com.lusle.soon.SearchCompanyActivity;
import com.simmorsal.library.ConcealerNestedScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class DateFragment extends Fragment {

    private ConcealerNestedScrollView mConcealerNSV;
    private OptRoundCardView mSearchView;
    private RelativeLayout mSearchBar;
    private static View viewgroup;

    final private int requestCode = 666;

    public static DateFragment newInstance() {
        Bundle args = new Bundle();

        DateFragment fragment=new DateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void getNestedScrollingEnabled() {
        if(viewgroup!=null){
            Log.d("Date", String.valueOf(viewgroup.isNestedScrollingEnabled()));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date, container, false);
        viewgroup = view;


        mSearchView = view.findViewById(R.id.fragment_searchbar);
        mSearchView.post(new Runnable() {
            @Override
            public void run() {
                mConcealerNSV.setFooterView(mSearchView, 50);
            }
        });


        mSearchBar = view.findViewById(R.id.fragment_common_searchbar);
        mSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchCompanyActivity.class);
                startActivityForResult(intent, requestCode);
            }
        });
        mConcealerNSV = view.findViewById(R.id.fragment_date_nestedscrollview);

        return view;
    }

}
