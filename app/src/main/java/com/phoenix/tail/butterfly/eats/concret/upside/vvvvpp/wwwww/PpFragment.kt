package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.wwwww

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseFragment
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentPpBinding
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils

class PpFragment : BaseFragment<FragmentPpBinding, PpViewModel>(){
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
        navigateTo(R.id.action_ppFragment_to_homeFragment)
    }
}