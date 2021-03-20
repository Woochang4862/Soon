package com.lusle.android.soon.View.Main.Company

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hugocastelani.waterfalltoolbar.WaterfallToolbar
import com.lusle.android.soon.Adapter.ManageCompanyListAdapter
import com.lusle.android.soon.Model.API.APIClient
import com.lusle.android.soon.Model.API.APIInterface
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.ItemTouchHelper.SimpleItemTouchHelperCallback
import com.lusle.android.soon.Util.Util
import com.lusle.android.soon.View.Dialog.MovieProgressDialog
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ManageCompanyFragment : Fragment(), ManageCompanyListAdapter.OnItemManageListener {
    private lateinit var waterfallToolbar: WaterfallToolbar
    private lateinit var companyList: RecyclerView
    private lateinit var saveBtn: TextView
    private var startList: ArrayList<Company>? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var doubleBackToExitPressedOnce = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manage_company, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        companyList = view.findViewById(R.id.activity_favorite_company_recyclerView)
        companyList.layoutManager = LinearLayoutManager(requireContext())
        val favoriteListActivityRecyclerAdapter = ManageCompanyListAdapter(this)
        companyList.adapter = favoriteListActivityRecyclerAdapter
        companyList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(favoriteListActivityRecyclerAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(companyList)
        waterfallToolbar = view.findViewById(R.id.waterfallToolbar)
        waterfallToolbar.recyclerView = companyList
        saveBtn = view.findViewById(R.id.saveBtn)
        saveBtn.setOnClickListener { _ : View? ->
            val dialog = MovieProgressDialog(requireContext())
            dialog.show()
            val type = object : TypeToken<ArrayList<Company?>?>() {}.type
            val list = Gson().toJson((companyList.adapter as ManageCompanyListAdapter?)!!.list, type)
            val pref = requireActivity().getSharedPreferences("pref", MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("favorite_company", list)
            editor.apply()
            dialog.dismiss()
            saveBtn.visibility = View.GONE
            startList = (companyList.adapter as ManageCompanyListAdapter?)!!.list
        }
        if (!Util.bindingData(requireContext(), companyList, "FavoriteMore")) {
            DynamicToast.makeError(requireContext(), "즐겨찾기 정보를 불러 올 수 없습니다.").show()
        }

        //TODO:캐스트 없이 잘 작동하는지 테스
        startList = (companyList.adapter as ManageCompanyListAdapter?)!!.list
    }

    /*override fun onBackPressed() {
        if (saveBtn.visibility == View.VISIBLE) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            DynamicToast.makeWarning(this, "저장하지 않으면 적용이 되지 않습니다. 괜찮다면 한 번더 눌러주세요").show()
            doubleBackToExitPressedOnce = true
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } else {
            finish()
        }
    }*/

    private fun showUndoSnackBar(deletedItem: Company, pos: Int) {
        Snackbar.make(companyList, deletedItem.name + "이(가) 삭제되었습니다.", Snackbar.LENGTH_LONG)
                .setAction("UNDO") { (companyList.adapter as ManageCompanyListAdapter?)!!.insertItem(deletedItem, pos) }.show()
    }

    private fun checkSaveBtn() {
        saveBtn.visibility = if ((companyList.adapter as ManageCompanyListAdapter?)!!.list == startList) View.GONE else View.VISIBLE
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder?) {
        if (viewHolder != null) {
            mItemTouchHelper!!.startDrag(viewHolder)
        }
    }

    override fun insertItem(item: Company, position: Int) {
        checkSaveBtn()
    }

    override fun onItemDismiss(deletedItem: Company, position: Int) {
        showUndoSnackBar(deletedItem, position)
        checkSaveBtn()
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("####", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result.token
                    val body = HashMap<String, String>()
                    body["company_id"] = deletedItem.id.toString()
                    body["token"] = token
                    APIClient.getClient().create(APIInterface::class.java).removeCompanyAlarm(body).enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {}
                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                })
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        checkSaveBtn()
    }
}