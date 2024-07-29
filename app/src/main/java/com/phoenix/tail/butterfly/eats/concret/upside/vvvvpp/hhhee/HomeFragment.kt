package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.hhhee

import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseFragment
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentHomeBinding
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.searchData
import com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.aaarr.PaperWebAdapter
import com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.wwwtt.CustomWebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.system.exitProcess

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(),
    CustomWebView.WebViewCallback {
    private lateinit var customWebView: CustomWebView
    private lateinit var markAdapter: PaperWebAdapter
    private var markListData: MutableList<CustomWebView.WebPageInfo>? = null
    private lateinit var historyAdapter: PaperWebAdapter
    private var historyListData: MutableList<CustomWebView.WebPageInfo>? = null

    private var isAllCheckMark = false
    private var isAllUnCheckHistory = false
    override val layoutId: Int
        get() = R.layout.fragment_home

    override val viewModelClass: Class<HomeViewModel>
        get() = HomeViewModel::class.java


    override fun setupViews() {
        customWebView = activity?.let { CustomWebView(it) }!!
        customWebView.setOnWebViewCallbackListener(this)
        setEditView()
        clickFun()
        binding.showPage = 1
        binding.showAdLoading = false
        binding.webContainer.addView(customWebView)
    }

    override fun onResume() {
        super.onResume()
        when (AAApp.appComponent.searchData) {
            "1" -> {
                binding.imgSearchLogo.setImageResource(R.drawable.ic_bing)
                binding.imgSearLogo.setImageResource(R.drawable.ic_bing)
            }

            "2" -> {
                binding.imgSearchLogo.setImageResource(R.drawable.ic_google)
                binding.imgSearLogo.setImageResource(R.drawable.ic_google)
            }

            "3" -> {
                binding.imgSearchLogo.setImageResource(R.drawable.ic_yahoo)
                binding.imgSearLogo.setImageResource(R.drawable.ic_google)
            }

            "4" -> {
                binding.imgSearchLogo.setImageResource(R.drawable.ic_duck)
                binding.imgSearLogo.setImageResource(R.drawable.ic_google)
            }

            else -> {
                binding.imgSearchLogo.setImageResource(R.drawable.ic_google)
                binding.imgSearLogo.setImageResource(R.drawable.ic_google)
            }
        }
    }

    private fun clickFun() {
        binding.viewMark.setOnClickListener {
            binding.showMenu = false
        }
        binding.aivHome.setOnClickListener {
            showClickAd {
                binding.showPage = 1
                binding.showMenu = false
            }
        }
        binding.aivHistory.setOnClickListener {
            binding.showMenu = false
            if (binding.showPage == 0) {
                customWebView.handleBackPress()
            } else {
                showClickAd {
                    initHistoryAdapter()
                    binding.showPage = 2
                }
            }
        }
        binding.aivMark.setOnClickListener {
            binding.showMenu = false
            if (binding.showPage == 0) {
                customWebView.goForwardPage()
            } else {
                showClickAd {
                    initMarksAdapter()
                    binding.showPage = 3
                }
            }
        }
        binding.aivMenu.setOnClickListener {
            binding.showMenu = true
        }
        binding.atvIns.setOnClickListener {
            showWebPage(DataUtils.ins_url)
        }
        binding.atvFb.setOnClickListener {
            showWebPage(DataUtils.fb_url)
        }
        binding.atvNf.setOnClickListener {
            showWebPage(DataUtils.netfilx_url)
        }
        binding.atvYtb.setOnClickListener {
            showWebPage(DataUtils.ytb_url)
        }
        binding.imgClear.setOnClickListener {
            binding.etHomeWeb.text?.clear()
        }
        binding.imgWebS.setOnClickListener {
            if (binding.imgClear.isVisible) {
                urlToWebView(binding.etHomeWeb)
            }
        }
        binding.tvRefresh.setOnClickListener {
            customWebView.refreshPage()
        }
        binding.tvAdd.setOnClickListener {
            activity?.let { it1 ->
                customWebView.saveWebsiteInfo(it1, true)
                Toast.makeText(requireContext(), "Saved Successfully", Toast.LENGTH_SHORT).show()
            }
            binding.showMenu = false
        }
        binding.tvMarks.setOnClickListener {
            showClickAd {
                binding.showMenu = true
                initMarksAdapter()
                binding.showPage = 3
                binding.showMenu = false
            }
        }
        binding.tvSEngine.setOnClickListener {
            navigateTo(R.id.action_homeFragment_to_searchFragment)
        }
        binding.tvShare.setOnClickListener {
            activity?.let { it1 -> viewModel.shareText(it1) }
        }
        binding.tvPrivacy.setOnClickListener {
            navigateTo(R.id.action_homeFragment_to_ppFragment)
        }

        binding.imgCheck.setOnClickListener {
            isAllCheckMark = !isAllCheckMark
            isShowAllSelected(isAllCheckMark)
        }
        binding.atvCancel.setOnClickListener {
            binding.llButton.isVisible = false
        }
        binding.atvDelete.setOnClickListener {
            activity?.let { activityContext ->
                viewModel.showDeleteDialog(activityContext) {
                    binding.llButton.isVisible = false
                    binding.imgCheck.setImageResource(R.drawable.ic_check)
                    val toBeRemoved = markListData?.filter { it.selected }?.toList() ?: emptyList()

                    if (toBeRemoved.isNotEmpty()) {
                        toBeRemoved.forEach { item ->
                            customWebView.deleteWebsiteInfo(activityContext, true, item.date)
                        }
                        markListData?.removeAll(toBeRemoved)
                        showNoData(markAdapter, markListData!!)
                    }
                }
            }
        }
        binding.imgCheckHistory.setOnClickListener {
            isAllUnCheckHistory = !isAllUnCheckHistory
            isShowAllSelectedHistory(isAllUnCheckHistory)
        }
        binding.atvClear.setOnClickListener {
            activity?.let { activityContext ->
                viewModel.showClearDialog(activityContext) {
                    binding.atvClear.isVisible = false
                    binding.imgCheckHistory.setImageResource(R.drawable.ic_check)
                    val toBeRemoved =
                        historyListData?.filter { it.selected }?.toList() ?: emptyList()

                    if (toBeRemoved.isNotEmpty()) {
                        toBeRemoved.forEach { item ->
                            customWebView.deleteWebsiteInfo(activityContext, false, item.date)
                        }
                        historyListData?.removeAll(toBeRemoved)
                        showNoData(historyAdapter, historyListData!!)
                    }
                }
            }
        }
    }

    private fun isShowAllSelected(isAllCheck: Boolean) {
        binding.llButton.isVisible = isAllCheck
        if (isAllCheck) {
            binding.imgCheck.setImageResource(R.drawable.ic_check_x)
        } else {
            binding.imgCheck.setImageResource(R.drawable.ic_check)
        }
        markListData?.forEach {
            it.selected = isAllCheck
        }
        markAdapter.notifyDataSetChanged()
    }

    private fun isShowAllSelectedHistory(isAllCheck: Boolean) {
        binding.atvClear.isVisible = isAllCheck
        if (isAllCheck) {
            binding.imgCheckHistory.setImageResource(R.drawable.ic_check_x)
        } else {
            binding.imgCheckHistory.setImageResource(R.drawable.ic_check)
        }
        historyListData?.forEach {
            it.selected = isAllCheck
        }
        historyAdapter.notifyDataSetChanged()
    }

    private fun initMarksAdapter() {
        markListData = customWebView.getWebsiteInfoList(true)
        binding.showNoMarkData = markListData.isNullOrEmpty()
        if (markListData.isNullOrEmpty()) return
        markAdapter = PaperWebAdapter(markListData!!)
        binding.rvMark.adapter = markAdapter
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvMark.layoutManager = layoutManager
        markAdapter.setOnItemClickListener(object : PaperWebAdapter.OnItemClickListener {
            override fun onItemClick(date: String) {
                binding.showPage = 0
                historyListData?.forEach {
                    if (it.date == date) {
                        customWebView.loadCustomWeb(it.url, false)
                    }
                }
            }
        })
        markAdapter.setOnItemSelectedListener(object : PaperWebAdapter.OnItemSelectedListener {
            override fun onItemSelected(date: String) {
                isAllCheckMark = true
                var hasSelected = false // Flag to track if any item is selected

                markListData?.forEach {
                    if (it.date == date) {
                        it.selected = !it.selected
                    }
                    if (!it.selected) {
                        isAllCheckMark = false
                    }
                    if (it.selected) {
                        hasSelected = true // Set flag if any item is selected
                    }
                }
                binding.llButton.isVisible = hasSelected // Directly set visibility based on flag
                markListData?.forEach {
                    if (it.selected) {
                        binding.llButton.isVisible = true
                    }
                }
                markAdapter.updateData(markListData!!)
                binding.imgCheck.setImageResource(
                    if (isAllCheckMark) R.drawable.ic_check_x else R.drawable.ic_check
                )
            }
        })
    }

    private fun initHistoryAdapter() {
        historyListData = customWebView.getWebsiteInfoList(false)
        binding.showNoHistoryData = historyListData.isNullOrEmpty()
        if (historyListData.isNullOrEmpty()) return
        historyAdapter = PaperWebAdapter(historyListData!!)
        binding.rvHistory.adapter = historyAdapter
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvHistory.layoutManager = layoutManager
        historyAdapter.setOnItemClickListener(object : PaperWebAdapter.OnItemClickListener {
            override fun onItemClick(date: String) {
                binding.showPage = 0
                historyListData?.forEach {
                    if (it.date == date) {
                        customWebView.loadCustomWeb(it.url, false)
                    }
                }
            }
        })
        historyAdapter.setOnItemSelectedListener(object : PaperWebAdapter.OnItemSelectedListener {
            override fun onItemSelected(date: String) {
                isAllUnCheckHistory = true
                var hasSelected = false

                historyListData?.forEach {
                    if (it.date == date) {
                        it.selected = !it.selected
                    }
                    if (!it.selected) {
                        isAllUnCheckHistory = false
                    }
                    if (it.selected) {
                        hasSelected = true
                    }
                }
                binding.atvClear.isVisible = hasSelected
                historyListData?.forEach {
                    if (it.selected) {
                        binding.atvClear.isVisible = true
                    }
                }
                historyAdapter.updateData(historyListData!!)
                binding.imgCheckHistory.setImageResource(
                    if (isAllUnCheckHistory) R.drawable.ic_check_x else R.drawable.ic_check
                )
            }
        })
    }

    private fun setBackGoUi() {
        if (binding.showPage != 0) return
        if (customWebView.isAtStartOfBackHistory()) {
            binding.aivHistory.setImageResource(R.drawable.ic_web_back_2)
        } else {
            binding.aivHistory.setImageResource(R.drawable.ic_web_back_1)
        }
        if (customWebView.isAtEndOfForwardHistory()) {
            binding.aivMark.setImageResource(R.drawable.ic_web_go_2)
        } else {
            binding.aivMark.setImageResource(R.drawable.ic_web_go_1)
        }
    }

    private fun setEditView() {
        binding.etHome.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                urlToWebView(binding.etHome)
                true
            } else {
                false
            }
        }
        binding.etHomeWeb.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                urlToWebView(binding.etHomeWeb)
                true
            } else {
                false
            }
        }
        binding.etHomeWeb.addTextChangedListener {
            binding.imgClear.isVisible = it.toString().trim().isNotEmpty()
        }


        binding.etWebMark.addTextChangedListener() {
            showSearchWebItem(it.toString(), markAdapter, markListData!!)
        }
    }

    private fun urlToWebView(
        view: AppCompatEditText
    ) {
        if (view.text.toString().trim().isNotEmpty()) {
            showWebPage(viewModel.searchUrl(view.text.toString()))
            view.text?.clear()
            activity?.let { viewModel.closeKeyboard(view, it) }
        }
    }

    private fun showSearchWebItem(
        seachString: String,
        adapter: PaperWebAdapter,
        listData: MutableList<CustomWebView.WebPageInfo>
    ) {
        if (listData.isNotEmpty()) {
            listData.forEach { all ->
                all.isGone = !((all.title).lowercase(Locale.getDefault()).contains(
                    seachString
                        .lowercase(Locale.getDefault())
                ))
                all.isGone = !((all.url).lowercase(Locale.getDefault()).contains(
                    seachString
                        .lowercase(Locale.getDefault())
                ))
            }
            showNoData(adapter, listData)
        }
    }

    private fun showNoData(
        adapter: PaperWebAdapter,
        allHistoryBeanData: MutableList<CustomWebView.WebPageInfo>
    ) {
        adapter.notifyDataSetChanged()
        var type = false
        allHistoryBeanData.forEach {
            if (!it.isGone) {
                type = true
            }
        }
        binding.showNoMarkData = !type
        binding.showNoHistoryData = !type
    }

    override fun observeViewModel() {
    }

    private fun showWebPage(url: String) {
        binding.showPage = 0
        customWebView.loadCustomWeb(url, true)
    }

    override fun customizeReturnKey() {
        if (binding.showPage == 1) {
            exitProcess(0)
            return
        }
        showBackAd {
            if (!customWebView.handleBackPress()) {
                binding.showPage = 1
                return@showBackAd
            }
            if (binding.showPage != 1) {
                binding.showPage = 1
                return@showBackAd
            }

        }

    }

    private fun showBackAd(nextFun: () -> Unit) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (AdManager.jumFunAd(AdManager.backHome)) {
                nextFun()
                return@launch
            }
            binding.showAdLoading = true
            var i = 0
            while (isActive) {
                i++
                if (AdManager.canShowAd(AdManager.backHome)) {
                    cancel()
                    binding.showAdLoading = false
                    AdManager.showAd(AdManager.backHome, requireActivity()) {
                        nextFun()
                    }
                }
                if (i >= 10) {
                    cancel()
                    binding.showAdLoading = false
                    nextFun()
                }
                delay(500)
            }
        }
    }

    private fun showClickAd(nextFun: () -> Unit) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (AdManager.jumFunAd(AdManager.clickInt)) {
                nextFun()
                return@launch
            }
            binding.showAdLoading = true
            var i = 0
            while (isActive) {
                i++
                if (AdManager.canShowAd(AdManager.clickInt)) {
                    cancel()
                    binding.showAdLoading = false
                    AdManager.showAd(AdManager.clickInt, requireActivity()) {
                        nextFun()
                    }
                }
                if (i >= 20) {
                    cancel()
                    binding.showAdLoading = false
                    nextFun()
                }
                delay(500)
            }
        }
    }

    override fun onPageStarted(url: String?) {
        setBackGoUi()
        binding.pbLoad.isVisible = true
    }

    override fun onPageFinished(url: String?) {
        setBackGoUi()
        binding.pbLoad.isVisible = false
    }

    override fun onProgressChanged(progress: Int) {
        binding.pbLoad.progress = progress
    }

}