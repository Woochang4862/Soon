package com.lusle.android.soon.Adapter.Contract;

import com.lusle.android.soon.Model.Schema.Genre;

import java.util.ArrayList;

public interface FragmentGenreAdapterContract {
    interface View extends BaseAdapterContract.View {

    }

    interface Model {

        void setList(ArrayList<Genre> mList);

        Genre getItem(int position);
    }
}
