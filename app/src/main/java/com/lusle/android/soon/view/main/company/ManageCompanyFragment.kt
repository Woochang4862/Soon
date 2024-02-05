package com.lusle.android.soon.view.main.company

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ManageCompanyFragment : Fragment(), ManageCompanyListAdapter.OnItemManageListener {
    private lateinit var backPressedCallback: OnBackPressedCallback
    private lateinit var companyAdapter: ManageCompanyListAdapter
    private var errorSnackBar: Snackbar? = null
    private var undoSnackBar: Snackbar? = null
    private var checkSaveSnackBar: Snackbar? = null
    private lateinit var companyList: RecyclerView
    private lateinit var saveBtn: TextView
    private lateinit var mItemTouchHelper: ItemTouchHelper

    private val viewModel by viewModels<CompanyViewModel> {
        CompanyViewModelFactory(
            FavoriteCompanyRepository(requireContext())
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (saveBtn.visibility == View.VISIBLE) {
                    if (checkSaveSnackBar?.isShown == true) {
                        findNavController().popBackStack()
                    }
                    showCheckSaveSnackBar()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        companyList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(companyAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(companyList)
        saveBtn = view.findViewById(R.id.saveBtn)
        saveBtn.setOnClickListener { _: View? ->
            val dialog = MovieProgressDialog(requireContext())
            dialog.show()
            undoSnackBar?.apply {
                if (isShown) {
                    dismiss()
                }
            }
            lifecycleScope.launch {
                viewModel.removeCompaniesAlarm()
                viewModel.saveCompaniesToLocal()
                dialog.dismiss()
                saveBtn.visibility = View.GONE
            }
        }
        companyAdapter?.list?.clear()
        viewModel.favoriteCompanyLiveData.observe(
            viewLifecycleOwner
        ) { favoriteCompany ->
            Log.d(TAG,"onViewCreated: favoriteCompanyLiveData - observe - $favoriteCompany"
            )
            companyAdapter?.apply {
                favoriteCompany?.let {
                    if (it.isEmpty()) {
                        onEmpty()
                    } else {
                        onNotEmpty()
                        list = it
                        notifyDataSetChanged()
                    }
                } ?: run {
                    companyAdapter.onEmpty()
                }
            }
        }
        viewModel.saveButtonState.observe(viewLifecycleOwner) { isVisible ->
            Log.d(TAG, "onViewCreated: saveButtonState Changed! - $isVisible")
            saveBtn.visibility = if (isVisible) View.VISIBLE else View.GONE

        }


        viewModel.loadFavoriteCompany()
        viewModel.checkSaveButtonVisibility()
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadFavoriteCompany()
    }

    private fun showCheckSaveSnackBar(){
        checkSaveSnackBar = Snackbar.make(
            requireView(),
            "변경사항이 저장되지 않았습니다.\n저장을 원하지 않는다면 뒤로가기를 한 번더 눌러주세요.",
            Snackbar.LENGTH_LONG
        )
            .setAction("저장") {
                saveBtn.performClick()
                checkSaveSnackBar?.dismiss()
            }
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)
        checkSaveSnackBar!!.show()
    }

    private fun showUndoSnackBar(deletedItem: Company, pos: Int) {
        undoSnackBar = Snackbar.make(
            requireView(),
            deletedItem.name + "이(가) 삭제되었습니다.",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("UNDO") {
                companyAdapter.insertItem(deletedItem, pos)
            }
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)
    undoSnackBar!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        undoSnackBar?.apply {
            if (isShown) {
                dismiss()
            }
        }
        checkSaveSnackBar?.apply {
            if (isShown) {
                dismiss()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder?) {
        if (viewHolder != null) {
            mItemTouchHelper.startDrag(viewHolder)
        }
    }

    override fun insertItem(item: Company, position: Int) {
        Log.d(TAG, "insertItem: $item, $position")
        viewModel.checkSaveButtonVisibility()
    }

    override fun onItemDismiss(deletedItem: Company, position: Int) {
        Log.d(TAG, "onItemDismiss: $deletedItem, $position")
        showUndoSnackBar(deletedItem, position)
        viewModel.checkSaveButtonVisibility()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Log.d(TAG, "onItemMove: $fromPosition, $toPosition")
        viewModel.checkSaveButtonVisibility()
    }

    companion object {
        val TAG = ManageCompanyFragment::class.java.simpleName
    }
}