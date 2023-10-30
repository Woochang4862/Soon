package com.lusle.android.soon.view.company_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
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
    private var query:String?=null

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
            lifecycleScope.launch {
                try {
                    viewModel.getFlow(it).collectLatest {
                        adapter.submitData(it)
                    }
                } catch (e:Exception){
                    e.printStackTrace()
                    //TODO:Exception Handling : Retry
                }
            }
        }?:run {
            //:TODO:Exception Handling
        }
    }
}