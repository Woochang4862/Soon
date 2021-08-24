package com.lusle.android.soon.View.Search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.Adapter.AllSearchActivityCompanyRecyclerViewAdapter
import com.lusle.android.soon.Adapter.AllSearchActivityMovieRecyclerViewAdapter
import com.lusle.android.soon.Adapter.Listener.OnCompanyBookMarkButtonClickListener
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Model.API.APIClient
import com.lusle.android.soon.Model.API.APIInterface
import com.lusle.android.soon.Model.Schema.*
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Utils
import com.lusle.android.soon.View.Alarm.AlarmSettingFragment
import com.lusle.android.soon.View.Detail.DetailActivity
import com.lusle.android.soon.View.Dialog.MovieProgressDialog
import com.lusle.android.soon.View.MovieList.MovieListActivity
import com.lusle.android.soon.View.Search.SearchActivity.OnQueryReceivedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class AllSearchFragment : Fragment(), OnQueryReceivedListener {
    private lateinit var companySection: CardView
    private lateinit var movieSection: CardView
    private lateinit var companyResults: TextView
    private lateinit var movieResults: TextView
    private lateinit var companyRecyclerView: RecyclerView
    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var companyAdapter: AllSearchActivityCompanyRecyclerViewAdapter
    private lateinit var movieAdapter: AllSearchActivityMovieRecyclerViewAdapter
    private lateinit var companyMoreBtn: RelativeLayout
    private lateinit var movieMoreBtn: RelativeLayout
    private var isCompanyExpanded = false
    private var isMovieExpanded = false
    private val apiInterface = APIClient.getClient().create(APIInterface::class.java)
    private var companyExpandArrayList: ArrayList<Company>? = null
    private var companyCollapseArrayList: ArrayList<Company>? = null
    private var movieExpandArrayList: ArrayList<Movie>? = null
    private var movieCollapseArrayList: ArrayList<Movie>? = null
    private lateinit var topSpace: View
    private lateinit var bottomSpace: View
    private lateinit var middleSpace: View
    private val sectionVisibility: MutableLiveData<Pair<Int, Int>> = MutableLiveData(Pair(View.GONE, View.GONE))
    private val searchTaskDone: MutableLiveData<Pair<Boolean, Boolean>> = MutableLiveData(Pair(false, false))
    private lateinit var dialog: MovieProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as SearchActivity?)!!.addQueryReceivedListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_all, container, false)

        dialog = MovieProgressDialog(requireContext())

        companySection = view.findViewById(R.id.company_section)
        movieSection = view.findViewById(R.id.movie_section)
        topSpace = view.findViewById(R.id.top_space)
        middleSpace = view.findViewById(R.id.middle_space)
        bottomSpace = view.findViewById(R.id.bottom_space)
        companyResults = view.findViewById(R.id.company_results_more)
        movieResults = view.findViewById(R.id.movie_results_more)
        companyRecyclerView = view.findViewById(R.id.company_recyclerview)
        movieRecyclerView = view.findViewById(R.id.movie_recyclerView)
        companyMoreBtn = view.findViewById(R.id.company_more)
        movieMoreBtn = view.findViewById(R.id.movie_more)

        companySection.visibility = View.GONE
        movieSection.visibility = View.GONE
        topSpace.visibility = View.GONE
        middleSpace.visibility = View.GONE
        bottomSpace.visibility = View.GONE

        sectionVisibility.observe(viewLifecycleOwner, { integerIntegerPair: Pair<Int, Int> ->
            topSpace.visibility = integerIntegerPair.first
            middleSpace.visibility = integerIntegerPair.second
            if (integerIntegerPair.first == View.GONE && integerIntegerPair.second == View.GONE) {
                bottomSpace.visibility = View.GONE
            } else {
                bottomSpace.visibility = View.VISIBLE
            }
        })
        searchTaskDone.observe(viewLifecycleOwner, {
            if (it.first and it.second){
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
                searchTaskDone.value = Pair(false, false)
            }
        })
        val companyLayoutManager = LinearLayoutManager(context)
        val movieLayoutManager = LinearLayoutManager(context)

        companyRecyclerView.layoutManager = companyLayoutManager
        movieRecyclerView.layoutManager = movieLayoutManager

        companyResults.setOnClickListener { (requireActivity() as SearchActivity).viewPager.currentItem = 1 }
        movieResults.setOnClickListener { (requireActivity() as SearchActivity).viewPager.currentItem = 2 }

        companyMoreBtn.setOnClickListener {
            if (isCompanyExpanded) {
                (companyMoreBtn.findViewById<View>(R.id.company_more_text) as TextView).text = "더 알아보기"
                (companyMoreBtn.findViewById<View>(R.id.company_more_image) as ImageView).setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_expand_more, null))
                companyAdapter.setList(companyCollapseArrayList)
                Utils.runLayoutAnimation(companyRecyclerView)
                isCompanyExpanded = false
            } else {
                (companyMoreBtn.findViewById<View>(R.id.company_more_text) as TextView).text = "접기"
                (companyMoreBtn.findViewById<View>(R.id.company_more_image) as ImageView).setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_expand_reverse, null))
                companyAdapter.setList(companyExpandArrayList)
                Utils.runLayoutAnimation(companyRecyclerView)
                isCompanyExpanded = true
            }
        }
        movieMoreBtn.setOnClickListener {
            if (isMovieExpanded) {
                (movieMoreBtn.findViewById<View>(R.id.movie_more_text) as TextView).text = "더 알아보기"
                (movieMoreBtn.findViewById<View>(R.id.movie_more_image) as ImageView).setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_expand_more, null))
                movieAdapter.setList(movieCollapseArrayList)
                Utils.runLayoutAnimation(movieRecyclerView)
                isMovieExpanded = false
            } else {
                (movieMoreBtn.findViewById<View>(R.id.movie_more_text) as TextView).text = "접기"
                (movieMoreBtn.findViewById<View>(R.id.movie_more_image) as ImageView).setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_expand_reverse, null))
                movieAdapter.setList(movieExpandArrayList)
                Utils.runLayoutAnimation(movieRecyclerView)
                isMovieExpanded = true
            }
        }
        return view
    }

    private val pairOfVisibilityOfSection: Pair<Int, Int>
        get() = Pair(companySection.visibility, movieSection.visibility)

    override fun onQueryReceived(query: String) {
        val pref = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        val list = pref.getString("favorite_company", "")
        val type = object : TypeToken<ArrayList<Company?>?>() {}.type
        var favoriteCompanyList = Gson().fromJson<ArrayList<Company?>>(list, type)
        if (favoriteCompanyList == null) favoriteCompanyList = ArrayList()
        companyAdapter = AllSearchActivityCompanyRecyclerViewAdapter(favoriteCompanyList, { view, position ->
            val intent = Intent(requireContext(), MovieListActivity::class.java)
            intent.putExtra("keyword", companyAdapter.getItem(position))
            startActivity(intent)
        }, object : OnEmptyListener {
            override fun onEmpty() {
                companySection.visibility = View.GONE
                sectionVisibility.postValue(pairOfVisibilityOfSection)
                Log.d("TAG", "companyAdapter 여기는?")
                companyMoreBtn.visibility = View.GONE
                companyRecyclerView.visibility = View.GONE
            }

            override fun onNotEmpty() {
                companyRecyclerView.visibility = View.VISIBLE
                companySection.visibility = View.VISIBLE
                sectionVisibility.postValue(pairOfVisibilityOfSection)
            }
        }, object : OnCompanyBookMarkButtonClickListener.View {
            override fun onCompanyBookMarkButtonClicked(favoriteCompanyList: ArrayList<Company?>) {
                val editor = pref.edit()
                editor.putString("favorite_company", Gson().toJson(favoriteCompanyList, type))
                editor.apply()
            }
        })
        companyRecyclerView.adapter = companyAdapter

        dialog.show()
        apiInterface.searchCompany(query, Utils.getRegionCode(context), 1).enqueue(object : Callback<CompanyResult?> {
            override fun onResponse(call: Call<CompanyResult?>, response: Response<CompanyResult?>) {
                response.body()?.let { result ->
                    if (result.totalResults == 0) {
                        companyAdapter.onEmpty()
                    } else {
                        Log.d("TAG", "컴퍼니 온 낫 엠티")
                        companyAdapter.onNotEmpty()
                        if (result.totalResults <= 3) {
                            companyAdapter.setList(result.results)
                        } else {
                            companyExpandArrayList = result.results
                            companyCollapseArrayList = ArrayList(result.results.subList(0, 4))
                            companyAdapter.setList(companyCollapseArrayList)
                            companyMoreBtn.visibility = View.VISIBLE
                        }
                        companyResults.text = "총 ${result.totalResults}개의 결과 더보기"
                    }
                    companyAdapter.notifyDataSetChanged()
                    searchTaskDone.postValue(Pair(true, searchTaskDone.value?.second ?: true))
                } ?: {
                    onFailure(call, Throwable())
                }()
            }

            override fun onFailure(call: Call<CompanyResult?>, t: Throwable) {
                companyAdapter.onEmpty()
                searchTaskDone.postValue(Pair(true, searchTaskDone.value?.second ?: true))
            }
        })
        apiInterface.genreList.enqueue(object : Callback<GenreResult> {
            override fun onResponse(call: Call<GenreResult>, response: Response<GenreResult?>) {
                response.body()?.let {
                    val genreList = it.genres
                    val genreMap = HashMap<Int, String>()
                    for (genre in genreList) {
                        genreMap[genre.id] = genre.name
                    }
                    movieAdapter = AllSearchActivityMovieRecyclerViewAdapter(
                            genreMap,
                            { v: View, i: Int ->
                                val intent = Intent(context, DetailActivity::class.java)
                                intent.putExtra("movie_id", movieAdapter.getItem(i)!!.id)
                                val poster = Pair.create(v.findViewById<View>(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerview_poster)))
                                val title = Pair.create(v.findViewById<View>(R.id.movie_list_recyclerView_title), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerView_title)))
                                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, poster, title)
                                startActivity(intent, options.toBundle())
                            },
                            object : OnEmptyListener {
                                override fun onEmpty() {
                                    movieRecyclerView.visibility = View.GONE
                                    movieSection.visibility = View.GONE
                                    sectionVisibility.postValue(pairOfVisibilityOfSection)
                                    Log.d("TAG", "movieAdapter 여기는?")
                                    movieMoreBtn.visibility = View.GONE
                                }

                                override fun onNotEmpty() {
                                    movieRecyclerView.visibility = View.VISIBLE
                                    movieSection.visibility = View.VISIBLE
                                    sectionVisibility.postValue(pairOfVisibilityOfSection)
                                }
                            },
                            { movie: Movie? ->
                                val intent = Intent(context, AlarmSettingFragment::class.java)
                                intent.putExtra("movie_info", movie)
                                startActivity(intent)
                            }
                    )
                    movieRecyclerView.adapter = movieAdapter

                    apiInterface.searchMovie(query, Utils.getRegionCode(context), 1).enqueue(object : Callback<MovieResult?> {
                        override fun onResponse(call: Call<MovieResult?>, response: Response<MovieResult?>) {
                            response.body()?.let { result ->
                                if (result.totalResults == 0) {
                                    movieAdapter.onEmpty()
                                } else {
                                    Log.d("TAG", "무비 온 낫 엠티")
                                    movieAdapter.onNotEmpty()
                                    if (result.totalResults <= 3) {
                                        movieAdapter.setList(result.results)
                                    } else {
                                        movieExpandArrayList = result.results
                                        movieCollapseArrayList = ArrayList(result.results.subList(0, 4))
                                        movieAdapter.setList(movieCollapseArrayList)
                                        movieMoreBtn.visibility = View.VISIBLE
                                    }
                                    movieResults.text = "총 ${result.totalResults}개의 결과 더보기"
                                    movieAdapter.notifyDataSetChanged()
                                }
                                searchTaskDone.postValue(Pair(searchTaskDone.value?.first ?: true, true))
                            } ?: {
                                onFailure(call, Throwable())
                            }()
                        }

                        override fun onFailure(call: Call<MovieResult?>, t: Throwable) {
                            companyAdapter.onEmpty()
                            searchTaskDone.postValue(Pair(searchTaskDone.value?.first ?: true, true))
                        }
                    })
                } ?: {
                    onFailure(call, Throwable())
                }()
            }

            override fun onFailure(call: Call<GenreResult>, t: Throwable) {
                companyAdapter.onEmpty()
                searchTaskDone.postValue(Pair(searchTaskDone.value?.first ?: true, true))
            }
        })
    }
}