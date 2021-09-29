package com.lusle.android.soon.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.lusle.android.soon.Adapter.Contract.CompanyAlarmSettingAdapterContract
import com.lusle.android.soon.Adapter.Holder.CompanyAlarmViewHolder
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.CircleTransform
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingContractor
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingPresenter
import com.squareup.picasso.Picasso
import java.util.*
import javax.annotation.Nonnull
import kotlin.collections.ArrayList

class CompanyAlarmSettingsAdapter(private var presenter: CompanyAlarmSettingContractor.Presenter) : BaseRecyclerAdapter<RecyclerView.ViewHolder?>(), CompanyAlarmSettingAdapterContract.View, CompanyAlarmSettingAdapterContract.Model {
    private var list: ArrayList<Company> = ArrayList()
    private val TAG = CompanyAlarmSettingsAdapter::class.java.simpleName
    private var onItemClickListener: OnItemClickListener? = null
    private var topics: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_company_alarm_recyclerview, parent, false)
        return CompanyAlarmViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val companyAlarmViewHolder = holder as CompanyAlarmViewHolder
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + list[position].logo_path)
                .centerCrop()
                .transform(CircleTransform())
                .fit()
                .error(R.drawable.ic_broken_image)
                .into(companyAlarmViewHolder.companyLogo)
        companyAlarmViewHolder.companyName.text = list[position].name
        try {
            companyAlarmViewHolder.companyAlarmActive.isEnabled = true
            companyAlarmViewHolder.companyAlarmActive.isChecked = false
            for (topic in topics) {
                Log.d("3", "onBindViewHolder: " + (topic.trim { it <= ' ' } == list[position].id.toString()))
                if (topic.trim { it <= ' ' } == list[position].id.toString()) {
                    Log.d(TAG, "onBindViewHolder: RUN!")
                    companyAlarmViewHolder.companyAlarmActive.isChecked = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            companyAlarmViewHolder.companyAlarmActive.isEnabled = false
        }
        companyAlarmViewHolder.companyAlarmActive.setOnCheckedChangeListener { _, isChecked ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result.toString()
                if (isChecked) {
                    presenter.addCompanyAlarm(token, list[position].id.toString())
                } else {
                    presenter.removeCompanyAlarm(token, list[position].id.toString())
                }
            })
        }
    }

    override fun getTopics(): ArrayList<String> {
        return topics
    }

    override fun setTopics(topics: ArrayList<String>) {
        this.topics = topics
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun setList(@Nonnull list: ArrayList<Company>) {
        this.list = list
    }

    override fun setPresenter(presenter: CompanyAlarmSettingPresenter) {
        this.presenter = presenter
    }

    override fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}