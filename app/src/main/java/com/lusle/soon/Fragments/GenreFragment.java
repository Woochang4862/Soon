package com.lusle.soon.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.lusle.soon.Adapter.FragmentGenreGenreRecyclerViewAdapter;
import com.lusle.soon.ContentResultGenreActivity;
import com.lusle.soon.Model.Genre;
import com.lusle.soon.R;
import com.lusle.soon.SearchCompanyActivity;
import com.simmorsal.library.ConcealerNestedScrollView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GenreFragment extends Fragment {

    private ConcealerNestedScrollView mConcealerNSV;
    private OptRoundCardView mSearchView;
    private RecyclerView mRecyclerView;
    private RelativeLayout mSearchBar;

    final private int requestCode = 666;

    public static GenreFragment newInstance() {
        Bundle args = new Bundle();

        GenreFragment fragment = new GenreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

        mSearchView = view.findViewById(R.id.fragment_searchbar);
        mSearchView.post(() -> mConcealerNSV.setFooterView(mSearchView, 50));


        mSearchBar = view.findViewById(R.id.fragment_common_searchbar);
        mSearchBar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchCompanyActivity.class);
            startActivityForResult(intent, requestCode);
        });
        mConcealerNSV = view.findViewById(R.id.fragment_genre_nestedscrollview);


        mRecyclerView = view.findViewById(R.id.fragment_genre_genre_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        FragmentGenreGenreRecyclerViewAdapter fragmentGenreGenreRecyclerViewAdapter = new FragmentGenreGenreRecyclerViewAdapter();
        mRecyclerView.setAdapter(fragmentGenreGenreRecyclerViewAdapter);
        fragmentGenreGenreRecyclerViewAdapter.SetOnClickLister((view1, position) -> {
            Intent intent = new Intent(getContext(), ContentResultGenreActivity.class);
            intent.putExtra("genre", fragmentGenreGenreRecyclerViewAdapter.getItem(position));
            startActivity(intent);
        });
        setDataIntoGenre(fragmentGenreGenreRecyclerViewAdapter);

        return view;
    }

    private void setDataIntoGenre(final FragmentGenreGenreRecyclerViewAdapter adapter) {

        new Thread(() -> {
            final ArrayList<Genre> list = new ArrayList<>();
            for (Genre genre :
                    new Genre[]{
                            new Genre(0, String.valueOf(R.drawable.ic_genre_action), "액션"),
                            new Genre(1, String.valueOf(R.drawable.ic_genre_animation), "애니메이션"),
                            new Genre(2, String.valueOf(R.drawable.ic_genre_comedy), "코미디"),
                            new Genre(3, String.valueOf(R.drawable.ic_genre_drama), "드라마"),
                            new Genre(4, String.valueOf(R.drawable.ic_genre_sf), "S.F."),
                            new Genre(5, String.valueOf(R.drawable.ic_genre_romance), "멜로"),
                            new Genre(6, String.valueOf(R.drawable.ic_genre_family), "가족"),
                            new Genre(7, String.valueOf(R.drawable.ic_genre_horor), "공포"),
                            new Genre(8, String.valueOf(R.drawable.ic_genre_crime), "범죄")
                    }) {
                list.add(genre);
            } //TODO:LOAD DATA from Server
            adapter.setmList(list);
            getActivity().runOnUiThread(() -> {
                //TODO:ProgressBar
                adapter.notifyDataSetChanged();
            });
        }).start();
    }
}
