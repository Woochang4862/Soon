package com.lusle.android.soon.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.R
import com.lusle.android.soon.util.CircleTransform
import com.lusle.android.soon.adapter.CompanyAlarmSettingsAdapter
import com.lusle.android.soon.adapter.listener.OnCheckedChangeListener
import com.squareup.picasso.Picasso

class CompanyAlarmViewHolder(itemView: View, private val onCheckedChangeListener:OnCheckedChangeListener) : RecyclerView.ViewHolder(itemView) {
    private var companyLogo: ImageView = itemView.findViewById(R.id.logo)
    private var companyName: TextView = itemView.findViewById(R.id.company_name)
    private var companyAlarmActive: SwitchMaterial = itemView.findViewById(R.id.company_alarm_active)

    fun bind(company:Company){
        itemView.setOnClickListener {
            if (companyAlarmActive.isEnabled) companyAlarmActive.isChecked =
                !companyAlarmActive.isChecked
        }

        Picasso
            .get()
            .load("https://image.tmdb.org/t/p/w500" + company.logo_path)
            .centerCrop()
            .transform(CircleTransform())
            .fit()
            .error(R.drawable.ic_broken_image)
            .into(companyLogo)
        companyName.text = company.name
        try {
            companyAlarmActive.isEnabled = true
            companyAlarmActive.isChecked = false
            for (topic in (bindingAdapter as CompanyAlarmSettingsAdapter).topics) {
                if (topic.trim { it <= ' ' } == company.id.toString()) {
                    companyAlarmActive.isChecked = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            companyAlarmActive.isEnabled = false
        }
        companyAlarmActive.setOnCheckedChangeListener { view, isChecked ->
            onCheckedChangeListener.onCheckedChangeListener(view, isChecked, company)
        }
    }
}
