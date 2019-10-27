package com.lusle.android.soon.Adapter.Contract;

import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Schema.Company;

import java.util.ArrayList;

public interface FragmentFavoriteCompanyAdapterContract {
    interface View extends BaseAdapterContract.View {
    }
    interface Model extends BaseAdapterContract.Model {
        Company getItem(int position);
        void setList(ArrayList<Company> list);
        void setOnItemClickListener(OnItemClickListener onItemClickListener);
    }
}
