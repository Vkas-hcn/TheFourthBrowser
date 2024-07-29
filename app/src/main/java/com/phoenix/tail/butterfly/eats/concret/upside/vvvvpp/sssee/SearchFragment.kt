package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.sssee

import androidx.lifecycle.lifecycleScope
import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseFragment
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentSearchBinding
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.searchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_search

    override val viewModelClass: Class<SearchViewModel>
        get() = SearchViewModel::class.java


    override fun setupViews() {
        binding.imgBack.setOnClickListener {
            customizeReturnKey()
        }
        binding.llBing.setOnClickListener {
            checkReturnKey("1")
        }
        binding.llGoogle.setOnClickListener {
            checkReturnKey("2")
        }
        binding.llYahoo.setOnClickListener {
            checkReturnKey("3")
        }
        binding.llDuck.setOnClickListener {
            checkReturnKey("4")
        }
    }

    private fun checkReturnKey(type: String) {
        AAApp.appComponent.searchData = type
        customizeReturnKey()
    }

    override fun observeViewModel() {
    }

    override fun customizeReturnKey() {
        showBackAd {
            navigateTo(R.id.action_searchFragment_to_homeFragment)
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
}