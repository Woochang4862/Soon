package com.lusle.android.soon.Adapter.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lusle.android.soon.R;

public class CompanyAlarmViewHolder extends RecyclerView.ViewHolder {

    public ImageView companyLogo;
    public TextView companyName;
    public SwitchMaterial companyAlarmActive;

    public CompanyAlarmViewHolder(@NonNull View itemView) {
        super(itemView);

        companyLogo = itemView.findViewById(R.id.logo);
        companyName = itemView.findViewById(R.id.company_name);
        companyAlarmActive = itemView.findViewById(R.id.company_alarm_active);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(companyAlarmActive.isEnabled())
                    companyAlarmActive.setChecked(!companyAlarmActive.isChecked());
            }
        });
    }
}
