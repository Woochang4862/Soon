package com.lusle.android.soon.Adapter.Contract;

import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;

public interface BaseAdapterContract {
    interface View{
        void notifyAdapter();
        void onEmpty();
        void onNotEmpty();
    }
    interface Model{
        void setOnEmptyListener(OnEmptyListener onEmptyListener);
        void setOnItemClickListener(OnItemClickListener onItemClickListener);
    }
}
