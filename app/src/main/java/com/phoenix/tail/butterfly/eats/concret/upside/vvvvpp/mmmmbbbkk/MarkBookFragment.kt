package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.mmmmbbbkk

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseFragment
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentMarkbookBinding
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentSearchBinding
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.searchData
import com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.aaarr.PaperWebAdapter
import com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.wwwtt.CustomWebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class MarkBookFragment : BaseFragment<FragmentMarkbookBinding, MarkBookViewModel>() {
    private lateinit var markAdapter: PaperWebAdapter
    private var markListData: MutableList<CustomWebView.WebPageInfo> = ArrayList()
    private lateinit var customWebView: CustomWebView

    override val layoutId: Int
        get() = R.layout.fragment_markbook

    override val viewModelClass: Class<MarkBookViewModel>
        get() = MarkBookViewModel::class.java


    override fun setupViews() {
        customWebView = activity?.let { CustomWebView(it) }!!
        binding.appCompatTextView.setOnClickListener {
            customizeReturnKey()
        }
        binding.atvDelete.setOnClickListener {
            activity?.let { activityContext ->
                viewModel.showDeleteDialog(activityContext) {
                    binding.llButton.isVisible = false
                    val toBeRemoved = markListData.filter { it.selected }.toList()

                    if (toBeRemoved.isNotEmpty()) {
                        toBeRemoved.forEach { item ->
                            customWebView.deleteWebsiteInfo(activityContext, true, item.date)
                        }
                        markListData.removeAll(toBeRemoved)
                        showNoData(markAdapter, markListData)
                    }
                }
            }
        }
        initMarksAdapter()
        setEditView()
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
    }
    private fun setEditView() {
        binding.etWebMark.addTextChangedListener {
            showSearchWebItem(it.toString(), markAdapter, markListData)
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
    override fun observeViewModel() {
    }

    override fun customizeReturnKey() {
        showBackAd {
            navigateTo(R.id.action_markBookFragment_to_homeFragment)
        }
    }
    private fun initMarksAdapter() {
        markListData = customWebView.getWebsiteInfoList(true)
        markListData.forEach {
            it.selected = false
        }
        binding.llButton.isVisible = false
        binding.showNoMarkData = markListData.isEmpty()
        markAdapter = PaperWebAdapter(markListData)
        binding.rvMark.adapter = markAdapter
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvMark.layoutManager = layoutManager
        markAdapter.setOnItemClickListener(object : PaperWebAdapter.OnItemClickListener {
            override fun onItemClick(date: String) {
                markListData.forEach {
                    if (it.date == date) {
                        customWebView.loadCustomWeb(it.url, false)
                    }
                }
            }
        })
        markAdapter.setOnItemSelectedListener(object : PaperWebAdapter.OnItemSelectedListener {
            override fun onItemSelected(date: String) {
                var hasSelected = false // Flag to track if any item is selected

                markListData.forEach {
                    if (it.date == date) {
                        it.selected = !it.selected
                    }
                    if (it.selected) {
                        hasSelected = true // Set flag if any item is selected
                    }
                }
                binding.llButton.isVisible = hasSelected // Directly set visibility based on flag
                markListData.forEach {
                    if (it.selected) {
                        binding.llButton.isVisible = true
                    }
                }
                markAdapter.updateData(markListData)
            }
        })
    }


    private fun showBackAd(nextFun: () -> Unit) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (AdManager.jumFunAd(AdManager.bbbbhhee)) {
                nextFun()
                return@launch
            }
            binding.showAdLoading = true
            var i = 0
            while (isActive) {
                i++
                if (i >= 2 && AdManager.canShowAd(AdManager.bbbbhhee)) {
                    cancel()
                    binding.showAdLoading = false
                    AdManager.showAd(AdManager.bbbbhhee, requireActivity()) {
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
}