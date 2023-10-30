package com.lusle.android.soon.view.main.setting.company_alarm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.model.source.FavoriteCompanyRepository
import com.lusle.android.soon.R
import com.lusle.android.soon.view.dialog.MovieProgressDialog
import com.lusle.android.soon.view.main.setting.company_alarm.CompanyAlarmSettingFragment.SnackBarType.*
import com.lusle.android.soon.adapter.CompanyAlarmSettingsAdapter
import com.lusle.android.soon.adapter.listener.OnCheckedChangeListener
import com.lusle.android.soon.adapter.listener.OnEmptyListener
import kotlinx.coroutines.launch

class CompanyAlarmSettingFragment : Fragment() {
    enum class SnackBarType {
        TOKEN,
        TOPICS,
        COMPANY,
        SUBSCRIBE,
        UNSUBSCRIBE,
    }

    private lateinit var tokenErrorSnackBar: Snackbar
    private lateinit var topicsErrorSnackBar: Snackbar
    private lateinit var companyErrorSnackBar: Snackbar
    private lateinit var subscribeErrorSnackBar: Snackbar
    private lateinit var unsubscribeErrorSnackBar: Snackbar
    private lateinit var aSwitch: SwitchMaterial
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompanyAlarmSettingsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var emptyViewGroup: FrameLayout
    private lateinit var emptyAnim: LottieAnimationView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var movieProgressDialog: MovieProgressDialog

    private var clickedByUser = true

    private val viewModel by viewModels<CompanyAlarmSettingViewModel> {
        CompanyAlarmSettingViewModelFactory(FavoriteCompanyRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_company_alarm_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenErrorSnackBar = Snackbar.make(
            requireView(),
            getString(R.string.invalid_token_error_msg),
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)
            .setAction("재시도") {
                load()
            }
        topicsErrorSnackBar = Snackbar.make(
            requireView(),
            getString(R.string.invalid_topics_error_msg),
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)
            .setAction("재시도") {
                load()
            }
        companyErrorSnackBar = Snackbar.make(
            requireView(),
            getString(R.string.invalid_company_error_msg),
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)
            .setAction("재시도") {
                load()
            }
        subscribeErrorSnackBar = Snackbar.make(
            requireView(),
            getString(R.string.failed_to_subscribe_company_msg),
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)
        unsubscribeErrorSnackBar = Snackbar.make(
            requireView(),
            getString(R.string.failed_to_unsubscribe_company_msg),
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)
        shimmerFrameLayout = view.findViewById(R.id.shimmer)
        aSwitch = view.findViewById(R.id.alarm_switch)
        emptyViewGroup = view.findViewById(R.id.list_empty_view)
        emptyAnim = view.findViewById(R.id.list_empty_anim)
        recyclerView = view.findViewById(R.id.company_alarm_list_recyclerView)
        movieProgressDialog = MovieProgressDialog(requireContext())
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        // Loading State Observe
        viewModel.subscribeLoadState.observe(viewLifecycleOwner) {
            subscribeLoading(it)
        }
        viewModel.companyLoadState.observe(viewLifecycleOwner) {
            companyLoading(it)
        }

        // Subscription Check for All Observe
        viewModel.allIsChecked.observe(viewLifecycleOwner) {
            clickedByUser = false
            aSwitch.isChecked = it
            clickedByUser = true
        }

        // Topic Observe
        viewModel.topics.observe(viewLifecycleOwner) { topics ->
            Log.d(TAG, "onViewCreated: $topics")
            adapter.topics = topics
        }

        // Company Observe
        viewModel.favoriteCompanyLiveData.observe(viewLifecycleOwner) { favoriteCompany ->
            favoriteCompany?.let {
                if (favoriteCompany.isEmpty()) {
                    adapter.onEmpty()
                } else {
                    adapter.onNotEmpty()
                    adapter.setList(favoriteCompany)
                }
            } ?: run {
                adapter.onEmpty()
            }
        }

        // All Switch
        aSwitch.setOnCheckedChangeListener { view: CompoundButton, isChecked: Boolean ->
            if (clickedByUser) {
                lifecycleScope.launch {
                    try {
                        viewModel.setAlarmSwitch(isChecked)
                        load()
                    } catch (e: SubscribeCompanyException) {
                        e.printStackTrace()
                        showErrorSnackBar(SUBSCRIBE)
                        clickedByUser = false
                        view.isChecked = !isChecked
                        clickedByUser = true
                    } catch (e: UnsubscribeCompanyException) {
                        e.printStackTrace()
                        showErrorSnackBar(UNSUBSCRIBE)
                        clickedByUser = false
                        view.isChecked = !isChecked
                        clickedByUser = true
                    }
                }
            }
        }

        // Adapter
        adapter = CompanyAlarmSettingsAdapter(object : OnCheckedChangeListener {
            override fun onCheckedChangeListener(
                view: CompoundButton,
                isChecked: Boolean,
                company: Company
            ) {
                lifecycleScope.launch {
                    try {
                        if (isChecked) {
                            viewModel.subscribeCompanyAlarm(company)
                        } else {
                            viewModel.unsubscribeCompanyAlarm(company)
                        }
                    } catch (e: SubscribeCompanyException) {
                        e.printStackTrace()
                        showErrorSnackBar(SUBSCRIBE)
                    } catch (e: UnsubscribeCompanyException) {
                        e.printStackTrace()
                        showErrorSnackBar(UNSUBSCRIBE)
                    }
                }
            }
        })
        adapter.setOnEmptyListener(object : OnEmptyListener {
            override fun onEmpty() {
                setRecyclerEmpty(true)
            }

            override fun onNotEmpty() {
                setRecyclerEmpty(false)
            }
        })
        recyclerView.adapter = adapter

        load()
    }

    private fun showErrorSnackBar(type: SnackBarType) {
        when (type) {
            TOKEN -> tokenErrorSnackBar.show()
            TOPICS -> topicsErrorSnackBar.show()
            COMPANY -> companyErrorSnackBar.show()
            SUBSCRIBE -> subscribeErrorSnackBar.show()
            UNSUBSCRIBE -> unsubscribeErrorSnackBar.show()
        }
    }

    private fun load() {
        lifecycleScope.launch {
            viewModel.apply {
                companyLoadState.value = true
                try {
                    loadToken()
                    checkSubscribedTopics()
                    loadFavoriteCompany()
                    checkAllIsChecked()
                } catch (e: TokenNotFoundException) {
                    e.printStackTrace()
                    showErrorSnackBar(TOKEN)
                } catch (e: SubscribedTopicsNotFoundException) {
                    e.printStackTrace()
                    showErrorSnackBar(TOPICS)
                } catch (e: CompanyNotFoundException) {
                    e.printStackTrace()
                    showErrorSnackBar(COMPANY)
                }
                companyLoadState.value = false
            }
        }
    }

    private fun companyLoading(isLoading: Boolean) {
        if (isLoading) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }


    fun setRecyclerEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            emptyViewGroup.visibility = View.VISIBLE
            if (!emptyAnim.isAnimating) emptyAnim.playAnimation()
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyViewGroup.visibility = View.GONE
            if (emptyAnim.isAnimating) emptyAnim.pauseAnimation()
        }
    }

    private fun subscribeLoading(isLoading: Boolean) {
        if (isLoading) {
            movieProgressDialog.show()
        } else {
            movieProgressDialog.hide()
        }
    }

    companion object {
        val TAG: String = CompanyAlarmSettingFragment::class.java.simpleName
    }
}