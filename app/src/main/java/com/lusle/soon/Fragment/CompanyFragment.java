package com.lusle.soon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.lusle.soon.Adapter.FragmentCompanyBookMarkRecyclerViewAdapter;
import com.lusle.soon.Adapter.FragmentCompanyResultRecyclerViewAdapter;
import com.lusle.soon.Model.Company;
import com.lusle.soon.Model.MovieDetail;
import com.lusle.soon.R;
import com.lusle.soon.SearchCompanyActivity;
import com.simmorsal.library.ConcealerNestedScrollView;
import com.xiaofeng.flowlayoutmanager.Alignment;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.lusle.soon.MainActivity.setBottomSheetBehaviorState;

public class CompanyFragment extends Fragment {

    private TextView companyName, mMoreBtn;
    private RecyclerView mFavorite;
    private ConcealerNestedScrollView mConcealerNSV;
    private CardView mSearchView;
    public RecyclerView resultList;

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
        setDataIntoFavorite(fragmentCompanyBookMarkRecyclerViewAdapter);


        resultList = view.findViewById(R.id.fragment_company_recyclerview_result);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        resultList.setLayoutManager(linearLayoutManager1);
        FragmentCompanyResultRecyclerViewAdapter resultRecyclerViewAdapter = new FragmentCompanyResultRecyclerViewAdapter(getContext());
        resultRecyclerViewAdapter.setOnItemClickListener(new FragmentCompanyResultRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        resultList.setAdapter(resultRecyclerViewAdapter);
        setDataIntoResult(resultRecyclerViewAdapter);


        mConcealerNSV = view.findViewById(R.id.fragment_company_nestedscrollview);
        mSearchView = view.findViewById(R.id.fragment_company_searchbar);
        mSearchView.post(new Runnable() {
            @Override
            public void run() {
                mConcealerNSV.setFooterView(mSearchView, 50);
            }
        });
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchCompanyActivity.class);
                startActivityForResult(intent, requestCode);
            }
        });


        mMoreBtn = view.findViewById(R.id.fragment_company_bookmark_more);
        mMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreBtn.getText().toString().equalsIgnoreCase("more")) {
                    mMoreBtn.setText("close");
                    mFavorite.setLayoutManager(flowLayoutManager);
                } else {
                    mMoreBtn.setText("more");
                    mFavorite.setLayoutManager(linearLayoutManager);
                }
            }
        });


        return view;
    }

    private void setDataIntoResult(final FragmentCompanyResultRecyclerViewAdapter resultRecyclerViewAdapter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<MovieDetail> tmp = new ArrayList<>();
                tmp.add(new MovieDetail("보헤미안 렙소디", "전체이용가", 1.0));
                tmp.add(new MovieDetail("b", "15세 이상 이용가", 2.0));
                tmp.add(new MovieDetail("c", "청소년 관람불가", 3.0));
                for (int i = 0; i < 100; i++) {
                    tmp.add(tmp.get(i % 3));
                } //TODO:LOAD DATA
                resultRecyclerViewAdapter.setmList(tmp);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultRecyclerViewAdapter.notifyDataSetChanged();
                        //TODO:ProgressBar
                    }
                });
            }
        }).start();
    }

    private void setDataIntoFavorite(final FragmentCompanyBookMarkRecyclerViewAdapter adapter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Company> list = new ArrayList<>();
                for (Company company :
                        new Company[]{
                                new Company(420, "/hUzeosd33nzE5MCNsZxCGEKTXaQ.png", "Marvel Studios"),
                                new Company(19551, "/2WpWp9b108hizjHKdA107hFmvQ5.png", "Marvel Enterprises"),
                                new Company(38679, "/7sD79XoadVfcgOVCjuEgQduob68.png", "Marvel Television"),
                                new Company(11106, "/nhI2D6OlNSrvNS18cf7m7b7N9vz.png", "Marvel Knights"),
                                new Company(2301, null, "Marvel Productions")
                        }) {
                    list.add(company);
                } //TODO:LOAD DATA
                final ArrayList<String> tmp = new ArrayList<>();
                for (Company item : list) {
                    tmp.add(item.getName());
                }
                if (adapter.setDatas(tmp) == -1) {
                    Toast.makeText(getContext(), "즐겨 찾기 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO:ProgressBar
                        adapter.notifyDataSetChanged();
                        companyName.setText("#" + adapter.getCompanyName(0));
                    }
                });
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestCode && resultCode == RESULT_OK) {
            setBottomSheetBehaviorState(BottomSheetBehavior.STATE_EXPANDED);
            //TODO:Set Result from API
            Toast.makeText(getContext(), data.getStringExtra("company_list"), Toast.LENGTH_SHORT).show();
        }
    }
}
