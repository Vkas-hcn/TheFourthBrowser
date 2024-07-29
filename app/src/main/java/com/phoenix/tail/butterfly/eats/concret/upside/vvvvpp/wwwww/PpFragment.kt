package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.wwwww

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseFragment
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentPpBinding
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PpFragment : BaseFragment<FragmentPpBinding, PpViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_pp

    override val viewModelClass: Class<PpViewModel>
        get() = PpViewModel::class.java


    override fun setupViews() {
        binding.iconBack.setOnClickListener {
            customizeReturnKey()
        }
        binding.webPp.apply {
            loadUrl(DataUtils.pp_url)
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return true
                }
            }
        }
    }

    override fun observeViewModel() {
    }

    override fun customizeReturnKey() {
        showBackAd {
            navigateTo(R.id.action_ppFragment_to_homeFragment)
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