package com.lusle.android.soon.View.Search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.Adapter.CompanyPagedListAdapter
import com.lusle.android.soon.Adapter.Listener.OnCompanyBookMarkButtonClickListener
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Model.API.CompanyApi
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.Model.Source.SearchCompanyPageKeyDataSource
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Utils
import com.lusle.android.soon.View.Dialog.MovieProgressDialog
import com.lusle.android.soon.View.MovieList.MovieListActivity
import com.lusle.android.soon.View.Search.SearchActivity.OnQueryReceivedListener
import io.reactivex.disposables.Disposable

class CompanySearchFragment : Fragment(), OnQueryReceivedListener {
    private val companyApi = CompanyApi.create()
    private val PAGE_SIZE: Int = 20
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyViewGroup: FrameLayout
    private lateinit var emptyAnim: LottieAnimationView
    private var adapter: CompanyPagedListAdapter? = null
    private lateinit var listDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext() as SearchActivity?)?.addQueryReceivedListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_company, container, false)
        recyclerView = view.findViewById(R.id.fragment_search_company_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        emptyViewGroup = view.findViewById(R.id.list_empty_view)
        emptyAnim = view.findViewById(R.id.list_empty_anim)
        return view
    }

    override fun onQueryReceived(query: String) {
        val pref = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        val list = pref.getString("favorite_company", "")
        val type = object : TypeToken<ArrayList<Company?>?>() {}.type
        var tempList = Gson().fromJson<ArrayList<Company?>>(list, type)
        if (tempList == null) tempList = ArrayList()
        adapter = CompanyPagedListAdapter(
                tempList,
                { _, position ->
                    val intent = Intent(requireContext(), MovieListActivity::class.java)
                    intent.putExtra("keyword", adapter?.getItem(position))
                    startActivity(intent)
                },
                object : OnEmptyListener {
                    override fun onEmpty() {
                        emptyViewGroup.visibility = View.VISIBLE
                        emptyAnim.playAnimation()
                    }

                    override fun onNotEmpty() {
                        emptyViewGroup.visibility = View.GONE
                        if (emptyAnim.isAnimating) emptyAnim.pauseAnimation()
                    }
                },
                object : OnCompanyBookMarkButtonClickListener.View {
                    override fun onCompanyBookMarkButtonClicked(favoriteCompanyList: ArrayList<Company?>) {
                        val editor = pref.edit()
                        editor.putString("favorite_company", Gson().toJson(favoriteCompanyList, type))
                        editor.apply()
                    }

                }
        )
        recyclerView.adapter = adapter

        val dialog = MovieProgressDialog(requireContext())
        dialog.show()

        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build()

        val factory = object : DataSource.Factory<Int, Company>() {
            override fun create(): DataSource<Int, Company> = SearchCompanyPageKeyDataSource(companyApi = companyApi, query, region = Utils.getRegionCode(requireContext()))
        }

        val builder = RxPagedListBuilder(factory, config)

        listDisposable = builder.buildObservable()
                .subscribe(
                        {
                            adapter?.onNotEmpty()
                            adapter?.submitList(it)
                            dialog.dismiss()
                            //TODO Shimmer Effect
                        },
                        { t: Throwable ->
                            t.printStackTrace()
                            adapter?.onEmpty()
                        }
                )
    }
}