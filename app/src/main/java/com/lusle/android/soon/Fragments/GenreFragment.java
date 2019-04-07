package com.lusle.android.soon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lusle.android.soon.API.APIClient;
import com.lusle.android.soon.API.APIInterface;
import com.lusle.android.soon.Adapter.FragmentGenreGenreRecyclerViewAdapter;
import com.lusle.android.soon.Model.Genre;
import com.lusle.android.soon.Model.GenreResult;
import com.lusle.android.soon.MovieListActivity;
import com.lusle.android.soon.MovieProgressDialog;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Utils.GridSpacingItemDecoration;
import com.lusle.android.soon.Utils.Utils;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private MovieProgressDialog dialog;
    private Activity activity;

    public static GenreFragment newInstance() {
        Bundle args = new Bundle();

        GenreFragment fragment = new GenreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

        dialog = new MovieProgressDialog(getContext());
        dialog.show();

        mRecyclerView = view.findViewById(R.id.fragment_genre_genre_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        int spanCount = 3; // 3 columns
        int spacing = Utils.dpToPx(getContext(), 67); // 50px
        boolean includeEdge = true;
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        FragmentGenreGenreRecyclerViewAdapter fragmentGenreGenreRecyclerViewAdapter = new FragmentGenreGenreRecyclerViewAdapter();
        mRecyclerView.setAdapter(fragmentGenreGenreRecyclerViewAdapter);
        fragmentGenreGenreRecyclerViewAdapter.SetOnClickLister((view1, position) -> {
            Intent intent = new Intent(getContext(), MovieListActivity.class);
            intent.putExtra("keyword", fragmentGenreGenreRecyclerViewAdapter.getItem(position));
            startActivity(intent);
        });
        setDataIntoGenre(fragmentGenreGenreRecyclerViewAdapter);

        return view;
    }

    private void setDataIntoGenre(final FragmentGenreGenreRecyclerViewAdapter adapter) {

        new Thread(() -> {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<GenreResult> call = apiInterface.getGenreList();
            call.enqueue(new Callback<GenreResult>() {
                @Override
                public void onResponse(Call<GenreResult> call, Response<GenreResult> response) {
                    ArrayList<Genre> list = response.body().getGenres();
                    adapter.setList(list);
                    activity.runOnUiThread(() -> {
                        //TODO:ProgressBar
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    });
                }

                @Override
                public void onFailure(Call<GenreResult> call, Throwable t) {
                    Log.e("MovieListError: ", t.getMessage());
                    dialog.dismiss();
                    DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
                }
            });
        }).start();
    }
}
