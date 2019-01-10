package com.lusle.soon.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lusle.soon.Adapter.FragmentGenreGenreRecyclerViewAdapter;
import com.lusle.soon.Model.Genre;
import com.lusle.soon.R;
import com.lusle.soon.SearchCompanyActivity;
import com.simmorsal.library.ConcealerNestedScrollView;

import java.util.ArrayList;

public class GenreFragment extends Fragment {

    private ConcealerNestedScrollView mConcealerNSV;
    private CardView mSearchView;
    private RecyclerView mRecyclerView;

    final private int requestCode = 666;

    public static GenreFragment newInstance() {
        Bundle args = new Bundle();

        GenreFragment fragment=new GenreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

        mSearchView = view.findViewById(R.id.fragment_genre_searchbar);
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
        mConcealerNSV = view.findViewById(R.id.fragment_genre_nestedscrollview);


        mRecyclerView = view.findViewById(R.id.fragment_genre_genre_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        FragmentGenreGenreRecyclerViewAdapter fragmentGenreGenreRecyclerViewAdapter = new FragmentGenreGenreRecyclerViewAdapter();
        mRecyclerView.setAdapter(fragmentGenreGenreRecyclerViewAdapter);
        fragmentGenreGenreRecyclerViewAdapter.SetOnClickLister(new FragmentGenreGenreRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {

            }
        });
        setDataIntoGenre(fragmentGenreGenreRecyclerViewAdapter);

        return view;
    }

    private void setDataIntoGenre(final FragmentGenreGenreRecyclerViewAdapter adapter){

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Genre> list = new ArrayList<>();
                for (Genre company :
                        new Genre[]{
                                new Genre(420, "/hUzeosd33nzE5MCNsZxCGEKTXaQ.png", "Marvel Studios"),
                                new Genre(19551, "/2WpWp9b108hizjHKdA107hFmvQ5.png", "Marvel Enterprises"),
                                new Genre(38679, "/7sD79XoadVfcgOVCjuEgQduob68.png", "Marvel Television"),
                                new Genre(11106, "/nhI2D6OlNSrvNS18cf7m7b7N9vz.png", "Marvel Knights"),
                                new Genre(2301, null, "Marvel Productions")
                        }) {
                    list.add(company);
                } //TODO:LOAD DATA
                adapter.setmList(list);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO:ProgressBar
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
