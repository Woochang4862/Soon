package com.lusle.android.soon.view.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.model.api.APIInterface
import com.lusle.android.soon.model.api.ApiClient
import com.lusle.android.soon.model.schema.Company
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.HashMap

class AllCompanyAlarmRemoveService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pref = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val list = pref.getString("favorite_company", "")
        val type = object : TypeToken<ArrayList<Company>>() {}.type
        val companyList = Gson().fromJson<ArrayList<Company>>(list, type)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("####", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }
            for (company in companyList) {
                // Get new Instance ID token
                val token = task.result.toString()
                val body = HashMap<String, String>()
                body["company_id"] = company.id.toString()
                body["token"] = token
                ApiClient.retrofit.create(APIInterface::class.java).removeCompanyAlarm(body).enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {}
                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
        })
        return START_STICKY
    }
}