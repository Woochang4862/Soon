package com.lusle.soon.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.lusle.soon.Adapter.FragmentCompanyBookMarkRecyclerViewAdapter;
import com.lusle.soon.Adapter.FragmentCompanyResultRecyclerViewAdapter;
import com.lusle.soon.Model.Company;
import com.lusle.soon.R;
import com.lusle.soon.SearchCompanyActivity;
import com.lusle.soon.Utils.Utils;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.simmorsal.library.ConcealerNestedScrollView;
import com.xiaofeng.flowlayoutmanager.Alignment;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.lusle.soon.MainActivity.setBottomSheetBehaviorState;

public class CompanyFragment extends Fragment {

    private TextView companyName, mMoreBtn;
    private RecyclerView mFavorite;
    private ConcealerNestedScrollView mConcealerNSV;
    private OptRoundCardView mSearchView;
    private RelativeLayout mSearchBar;
    public RecyclerView mMovieList;

    final private int requestCode = 666;

    public static CompanyFragment newInstance() {
        Bundle args = new Bundle();

        CompanyFragment fragment = new CompanyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company, container, false);


        companyName = view.findViewById(R.id.fragment_company_name);


        mFavorite = view.findViewById(R.id.fragment_company_favorite);
        final FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAlignment(Alignment.LEFT);
        flowLayoutManager.setAutoMeasureEnabled(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mFavorite.setLayoutManager(linearLayoutManager);
        final FragmentCompanyBookMarkRecyclerViewAdapter fragmentCompanyBookMarkRecyclerViewAdapter = new FragmentCompanyBookMarkRecyclerViewAdapter();
        fragmentCompanyBookMarkRecyclerViewAdapter.setOnItemClickListener(new FragmentCompanyBookMarkRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                companyName.setText(((TextView) view.findViewById(R.id.bookmark_recyclerview_item_company_name)).getText());
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getContext(), "삭제 기능이 준비 되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        mFavorite.setAdapter(fragmentCompanyBookMarkRecyclerViewAdapter);
        if(!Utils.bindingData(getContext(), mFavorite, "Favorite")) DynamicToast.makeError(getContext(),"즐겨찾기 정보를 불러 올 수 없습니다.").show();


        mMovieList = view.findViewById(R.id.fragment_company_recyclerview_result);
        mMovieList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        mMovieList.setLayoutManager(linearLayoutManager1);
        FragmentCompanyResultRecyclerViewAdapter resultRecyclerViewAdapter = new FragmentCompanyResultRecyclerViewAdapter();
        resultRecyclerViewAdapter.setOnItemClickListener(new FragmentCompanyResultRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        mMovieList.setAdapter(resultRecyclerViewAdapter);
        if (!Utils.bindingData(getContext(), mMovieList, "Company"))
            DynamicToast.makeError(getContext(), "영화 정보를 불러 올 수 없습니다.").show();


        mConcealerNSV = view.findViewById(R.id.fragment_company_nestedscrollview);
        mSearchView = view.findViewById(R.id.fragment_searchbar);
        mSearchView.post(() -> mConcealerNSV.setFooterView(mSearchView, 50));


        mSearchBar = view.findViewById(R.id.fragment_common_searchbar);
        mSearchBar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchCompanyActivity.class);
            startActivityForResult(intent, requestCode);
        });


        mMoreBtn = view.findViewById(R.id.fragment_company_bookmark_more);
        mMoreBtn.setOnClickListener(v -> {
            if (mMoreBtn.getText().toString().equalsIgnoreCase("더보기")) {
                mMoreBtn.setText("닫기");
                mFavorite.setLayoutManager(flowLayoutManager);
            } else {
                mMoreBtn.setText("더보기");
                mFavorite.setLayoutManager(linearLayoutManager);
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestCode && resultCode == RESULT_OK) {
            setBottomSheetBehaviorState(BottomSheetBehavior.STATE_EXPANDED);
            //TODO:Set Result from API
            ArrayList companyList = (ArrayList<Company>) data.getSerializableExtra("company_list");
            Toast.makeText(getContext(), String.valueOf(companyList.size()) + "개의 제작사가 선택됨", Toast.LENGTH_SHORT).show();
        }
    }
}
