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
            binding.showPaPageLoading = true
            loadUrl(DataUtils.pp_url)
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.showPaPageLoading = false
                }
            }
        }
    }

    override fun observeViewModel() {
    }

    override fun customizeReturnKey() {
        if(binding?.showPaPageLoading!!){
            binding.showPaPageLoading = false
            return
        }
        showBackAd {
            navigateTo(R.id.action_ppFragment_to_homeFragment)
        }
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