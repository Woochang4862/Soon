package com.lusle.android.soon.view.company_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.CompanyPagedListAdapter
import com.lusle.android.soon.adapter.decoration.CompanyItemDecoration
import com.lusle.android.soon.model.source.RegionCodeRepository
import com.lusle.android.soon.view.movie_list.MovieListActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CompanyListActivity : AppCompatActivity() {

    private lateinit var companyListRecyclerView: RecyclerView
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter: CompanyPagedListAdapter
    private var query: String? = null

    private var retryNumber = 0
    private val MAX_RETRY_NUM = 5

    private val viewModel by viewModels<CompanyListViewModel> {
        CompanyListViewModelFactory(RegionCodeRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_list)

        companyListRecyclerView = findViewById(R.id.company_list)
        layoutManager = GridLayoutManager(this, 3)
        if (companyListRecyclerView.itemDecorationCount == 0)
            companyListRecyclerView.addItemDecoration(CompanyItemDecoration(this))
        companyListRecyclerView.layoutManager = layoutManager
        adapter = CompanyPagedListAdapter { view, position ->
            val intent = Intent(this, MovieListActivity::class.java)
            intent.putExtra("keyword", adapter.getCompanyItem(position))
            startActivity(intent)
        }
        companyListRecyclerView.adapter = adapter

        query = intent.getStringExtra("query")

        query?.let {
            load(it)
        } ?: run {
            AlertDialog.Builder(this)
                .setMessage("제작사 정보를 불러오는 데 문제가 생겼습니다.")
                .setNegativeButton("닫기") {dialogInterface, i->
                    finish()
                }
                .show()
        }
    }

    private fun load(query: String) {
        try {
            lifecycleScope.launch {
                viewModel.getFlow(query).collectLatest {
                    adapter.submitData(it)
                }
            }
        } catch (e:Exception){
            e.printStackTrace()
            Log.d(TAG, "load: ")
            AlertDialog.Builder(this)
                .setMessage("제작사 정보를 불러오는 데 문제가 생겼습니다.\n다시 시도하시겠습니까?")
                .setPositiveButton("제시도"){dialogInterface, i->
                    if (retryNumber <= MAX_RETRY_NUM) {
                        retryNumber += 1
                        load(query)
                    }
                    retryNumber = 0
                }
                .setNegativeButton("아니요") {dialogInterface, i->
                    finish()
                }
                .show()
        }
    }

    companion object {
        val TAG = CompanyListActivity::class.java.simpleName
    }
}