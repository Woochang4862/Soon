package com.lusle.android.soon.View.Main.Setting.CompanyAlarm;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.lusle.android.soon.Adapter.CompanyAlarmSettingsAdapter;
import com.lusle.android.soon.Model.Contract.SubscribeCheckDataRemoteSourceContract;
import com.lusle.android.soon.Model.Source.CompanyAlarmManagerRemoteSource;
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource;
import com.lusle.android.soon.Model.Source.SubscribeCheckDataRemoteSource;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.Util;
import com.lusle.android.soon.View.BaseActivity;
import com.lusle.android.soon.View.Dialog.MovieProgressDialog;
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingContractor;
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingPresenter;

import java.util.ArrayList;

public class CompanyAlarmSettingActivity extends BaseActivity implements CompanyAlarmSettingContractor.View {

    private Switch aSwitch;
    private RecyclerView recyclerView;
    private CompanyAlarmSettingsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private FrameLayout emptyViewGroup;
    private LottieAnimationView emptyAnim;
    private CompanyAlarmSettingPresenter presenter;
    private MovieProgressDialog dialog;

    private boolean clickedByUser = true;
    private boolean reload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_alarm_setting);

        dialog = new MovieProgressDialog(this);

        presenter = new CompanyAlarmSettingPresenter();
        presenter.attachView(this);
        presenter.setModel(FavoriteCompanyDataLocalSource.getInstance());
        presenter.setModel(SubscribeCheckDataRemoteSource.getInstance());
        presenter.setModel(CompanyAlarmManagerRemoteSource.getInstance());

        aSwitch = findViewById(R.id.alarm_switch);
        emptyViewGroup = findViewById(R.id.list_empty_view);
        emptyAnim = findViewById(R.id.list_empty_anim);
        recyclerView = findViewById(R.id.company_alarm_list_recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        showDialog(true);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        presenter.checkSubscribedTopics(token);
                    }
                });
        presenter.setOnFinishedListener(new SubscribeCheckDataRemoteSourceContract.Model.OnFinishedListener() {
            @Override
            public void onFinished(ArrayList<String> topics) {
                adapter = new CompanyAlarmSettingsAdapter(presenter, topics);
                presenter.setAdapterView(adapter);
                presenter.setAdapterModel(adapter);
                presenter.setOnEmptyListener();
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(CompanyAlarmSettingActivity.this, DividerItemDecoration.VERTICAL));
                presenter.loadItems();
                showDialog(false);
                Util.runLayoutAnimation(recyclerView);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                showDialog(false);
            }
        });

        setAlarmSwitch();
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (clickedByUser) {

            }
        });
    }

    private void setAlarmSwitch() {
        /*clickedByUser = false;
        boolean checked = false;
        for (Alarm alarm : presenter.getAlarms()) {
            if (alarm.isActive()) {
                checked = true;
                break;
            }
        }
        aSwitch.setChecked(checked);
        clickedByUser = true;*/
    }

    private void reload() {
        presenter.loadItems();
        Util.runLayoutAnimation(recyclerView);
        reload = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (reload) {
            reload();
            setAlarmSwitch();
        }
    }

    @Override
    public void setRecyclerEmpty(boolean isEmpty) {
        if (isEmpty) {
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
    protected void onPause() {
        super.onPause();
        reload = true;
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showDialog(boolean show) {
        if (show) dialog.show();
        else dialog.dismiss();
    }

}
