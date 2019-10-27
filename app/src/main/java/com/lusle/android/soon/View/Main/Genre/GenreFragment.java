package com.lusle.android.soon.View.Main.Genre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lusle.android.soon.Adapter.FragmentGenreGenreRecyclerViewAdapter;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.GridSpacingItemDecoration;
import com.lusle.android.soon.Util.Util;
import com.lusle.android.soon.View.Dialog.MovieProgressDialog;
import com.lusle.android.soon.View.Main.Genre.Presenter.GenreContractor;
import com.lusle.android.soon.View.Main.Genre.Presenter.GenrePresenter;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GenreFragment extends Fragment implements GenreContractor.View {

    private RecyclerView mRecyclerView;

    private MovieProgressDialog dialog;
    private GenrePresenter presenter;
    private GridLayoutManager gridLayoutManager;
    private FragmentGenreGenreRecyclerViewAdapter adapter;

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

        presenter = new GenrePresenter();
        presenter.attachView(this);
        presenter.setModel(GenreDataRemoteSource.getInstance());

        dialog = new MovieProgressDialog(getContext());

        mRecyclerView = view.findViewById(R.id.fragment_genre_genre_list);

        gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, Util.dpToPx(getContext(), 67), true));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new FragmentGenreGenreRecyclerViewAdapter();
        presenter.setAdapterView(adapter);
        presenter.setAdapterModel(adapter);
        mRecyclerView.setAdapter(adapter);
        presenter.setOnItemClickListener();
        presenter.loadItems();

        return view;
    }

    @Override
    public void runRecyclerViewAnimation() {
        Util.runLayoutAnimation(mRecyclerView);
    }

    @Override
    public void showDialog(boolean show) {
        if(show) dialog.show();
        else dialog.dismiss();
    }

    @Override
    public void showErrorToast() {
        DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }
}
