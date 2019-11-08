package com.lusle.android.soon.View.Main.Setting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.lusle.android.soon.Adapter.AlarmSettingsAdapter;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.Model.Source.AlarmDataLocalSource;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.Util;
import com.lusle.android.soon.View.Main.Setting.Presenter.SettingContact;
import com.lusle.android.soon.View.Main.Setting.Presenter.SettingPresenter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SettingFragment extends Fragment implements SettingContact.View {

    private Switch aSwitch;
    private RecyclerView recyclerView;
    private AlarmSettingsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private FrameLayout emptyViewGroup;
    private LottieAnimationView emptyAnim;

    private boolean clickedByUser = true;

    private SettingPresenter presenter;

    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        presenter = new SettingPresenter();
        presenter.attachView(this);
        presenter.setModel(AlarmDataLocalSource.getInstance(getContext()));

        aSwitch = view.findViewById(R.id.alarm_switch);
        emptyViewGroup = view.findViewById(R.id.list_empty_view);
        emptyAnim = view.findViewById(R.id.list_empty_anim);
        recyclerView = view.findViewById(R.id.alarm_list_recyclerView);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AlarmSettingsAdapter();
        presenter.setAdapterView(adapter);
        presenter.setAdapterModel(adapter);
        presenter.setOnItemClickListener();
        presenter.setOnEmptyListener();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        setAlarmSwitch();
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (clickedByUser) {
                ArrayList<Alarm> alarms = presenter.getAlarms();
                for (Alarm alarm : alarms)
                    alarm.setActive(isChecked);
                presenter.setAlarms(alarms);
                Toast.makeText(getContext(), "알림이 " + (isChecked ? "전부 켜졌습니다" : "전부 꺼졌습니다"), Toast.LENGTH_SHORT).show();
                reload();
            }
        });

        presenter.loadItems();
        Util.runLayoutAnimation(recyclerView);

        return view;
    }

    private void setAlarmSwitch() {
        clickedByUser = false;
        boolean checked = false;
        for (Alarm alarm : presenter.getAlarms()) {
            if (alarm.isActive()) {
                checked = true;
                break;
            }
        }
        aSwitch.setChecked(checked);
        clickedByUser = true;
    }

    private void reload(){
        presenter.loadItems();
        Util.runLayoutAnimation(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
        setAlarmSwitch();
    }

    @Override
    public void setRecyclerEmpty(boolean isEmpty) {
        if(isEmpty){
            recyclerView.setVisibility(View.GONE);
            emptyViewGroup.setVisibility(View.VISIBLE);
            if (!emptyAnim.isAnimating()) emptyAnim.playAnimation();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewGroup.setVisibility(View.GONE);
            if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
        }
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }
}
