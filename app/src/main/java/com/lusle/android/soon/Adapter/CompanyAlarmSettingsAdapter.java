package com.lusle.android.soon.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.lusle.android.soon.Adapter.Contract.CompanyAlarmSettingAdapterContract;
import com.lusle.android.soon.Adapter.Holder.CompanyAlarmViewHolder;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Schema.Company;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.CircleTransform;
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingContractor;
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CompanyAlarmSettingsAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> implements CompanyAlarmSettingAdapterContract.View, CompanyAlarmSettingAdapterContract.Model {

    private ArrayList<Company> list;
    private String TAG = CompanyAlarmSettingsAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;
    private CompanyAlarmSettingContractor.Presenter presenter;
    private ArrayList<String> topics;

    public CompanyAlarmSettingsAdapter(CompanyAlarmSettingContractor.Presenter presenter, ArrayList<String> topics) {
        this.presenter = presenter;
        this.topics = topics;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company_alarm_recyclerview, parent, false);
        return new CompanyAlarmViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CompanyAlarmViewHolder companyAlarmViewHolder = (CompanyAlarmViewHolder) holder;
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + list.get(position).getLogo_path())
                .centerCrop()
                .transform(new CircleTransform())
                .fit()
                .error(R.drawable.ic_broken_image)
                .into(companyAlarmViewHolder.companyLogo);
        companyAlarmViewHolder.companyName.setText(list.get(position).getName());
        try {
            companyAlarmViewHolder.companyAlarmActive.setEnabled(true);
            companyAlarmViewHolder.companyAlarmActive.setChecked(false);
            for (String topic :
                    topics) {
                Log.d("3", "onBindViewHolder: " + topic.trim().equals(list.get(position).getId().toString()));
                if(topic.trim().equals(list.get(position).getId().toString())){
                    Log.d(TAG, "onBindViewHolder: RUN!");
                    companyAlarmViewHolder.companyAlarmActive.setChecked(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            companyAlarmViewHolder.companyAlarmActive.setEnabled(false);
        }
        companyAlarmViewHolder.companyAlarmActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                if (isChecked) {
                                    presenter.addCompanyAlarm(token, list.get(position).getId().toString());
                                } else {
                                    presenter.removeCompanyAlarm(token, list.get(position).getId().toString());
                                }
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    @Override
    public void setList(ArrayList<Company> list) {
        this.list = list;
    }

    @Override
    public void setPresenter(CompanyAlarmSettingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
