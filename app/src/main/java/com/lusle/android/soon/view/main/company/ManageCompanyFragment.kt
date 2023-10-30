package com.lusle.android.soon.view.main.company

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.adapter.ManageCompanyListAdapter
import com.lusle.android.soon.model.api.APIInterface
import com.lusle.android.soon.model.api.ApiClient
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.model.source.FavoriteCompanyRepository
import com.lusle.android.soon.R
import com.lusle.android.soon.util.ItemTouchHelper.SimpleItemTouchHelperCallback
import com.lusle.android.soon.view.dialog.MovieProgressDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ManageCompanyFragment : Fragment(), ManageCompanyListAdapter.OnItemManageListener {
    private var companyAdapter: ManageCompanyListAdapter? = null
    private lateinit var errorSnackBar: Snackbar
    private var undoSnackBar: Snackbar? = null
    private lateinit var companyList: RecyclerView
    private lateinit var saveBtn: TextView
    private var startList: ArrayList<*>? = null
    private var mItemTouchHelper: ItemTouchHelper? = null

    private val viewModel by viewModels<CompanyViewModel> {
        CompanyViewModelFactory(
            FavoriteCompanyRepository(requireContext())
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manage_company, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorSnackBar = Snackbar.make(requireView(), "즐겨찾기 정보를 불러 올 수 없습니다.", Snackbar.LENGTH_SHORT)
                .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
                .setGestureInsetBottomIgnored(true)

        companyList = view.findViewById(R.id.activity_favorite_company_recyclerView)
        companyList.layoutManager = LinearLayoutManager(requireContext())
        companyAdapter = ManageCompanyListAdapter(this)
        companyList.adapter = companyAdapter
        companyList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(companyAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(companyList)
        saveBtn = view.findViewById(R.id.saveBtn)
        saveBtn.setOnClickListener { _ : View? ->
            val dialog = MovieProgressDialog(requireContext())
            dialog.show()
            undoSnackBar?.let {
                if(it.isShown){
                    it.dismiss()
                }
            }
            val type = object : TypeToken<ArrayList<Company?>?>() {}.type
            val list = Gson().toJson((companyList.adapter as ManageCompanyListAdapter?)?.list, type)
            val pref = requireActivity().getSharedPreferences("pref", MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("favorite_company", list)
            editor.apply()
            dialog.dismiss()
            saveBtn.visibility = View.GONE
            startList = (companyList.adapter as ManageCompanyListAdapter?)?.list
        }
        companyAdapter?.list?.clear()
        viewModel.favoriteCompanyLiveData.observe(
            viewLifecycleOwner
        ) { favoriteCompany ->
            Log.d("ManageCompanyFragment", "onViewCreated: $favoriteCompany")
            favoriteCompany?.let {
                if (favoriteCompany.isEmpty()) {
                    companyAdapter?.onEmpty()
                } else {
                    companyAdapter?.onNotEmpty()
                    companyAdapter?.list = favoriteCompany
                    startList = favoriteCompany
                    companyAdapter?.notifyDataSetChanged()
                }
            } ?: run {
                companyAdapter?.onEmpty()
            }
        }

        viewModel.loadFavoriteCompany()
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadFavoriteCompany()
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
        undoSnackBar = Snackbar.make(requireView(), deletedItem.name + "이(가) 삭제되었습니다.", Snackbar.LENGTH_INDEFINITE)
                .setAction("UNDO") {
                    (companyList.adapter as ManageCompanyListAdapter?)?.insertItem(deletedItem, pos)
                }
                .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
                .setGestureInsetBottomIgnored(true)
        undoSnackBar?.show()
    }

    override fun onPause() {
        super.onPause()
        undoSnackBar?.let {
            if(it.isShown){
                it.dismiss()
            }
        }
    }

    private fun checkSaveBtn() {
        saveBtn.visibility = if ((companyList.adapter as ManageCompanyListAdapter).list == startList) View.GONE else View.VISIBLE
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder?) {
        if (viewHolder != null) {
            mItemTouchHelper?.startDrag(viewHolder)
        }
    }

    override fun insertItem(item: Company, position: Int) {
        checkSaveBtn()
    }

    override fun onItemDismiss(deletedItem: Company, position: Int) {
        showUndoSnackBar(deletedItem, position)
        checkSaveBtn()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("####", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result.toString()
            val body = HashMap<String, String>()
            body["company_id"] = deletedItem.id.toString()
            body["token"] = token
            ApiClient.retrofit.create(APIInterface::class.java).removeCompanyAlarm(body).enqueue(object : Callback<ResponseBody?> {
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