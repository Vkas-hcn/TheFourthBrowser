package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.wwwtt

import android.annotation.SuppressLint
import android.webkit.*
import java.net.URL
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.InputStream

@SuppressLint("SetJavaScriptEnabled")
class CustomWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(context, attrs) {
    interface WebViewCallback {
        fun onPageStarted(url: String?)
        fun onPageFinished(url: String?)
        fun onProgressChanged(progress: Int)
    }

    private var callback: WebViewCallback? = null
    private lateinit var loadUrl: String
    private var isHistory: Boolean = false
    private val customWebViewClient = object : WebViewClient() {
        private val visitedUrls = mutableListOf<String>()

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (loadUrl == url) {
                view.loadUrl(url)
            } else {
                return super.shouldOverrideUrlLoading(view, url)
            }
            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            url?.let {
                if (!isRedirected(it)) {
                    visitedUrls.add(it)
                }
            }
            callback?.onPageStarted(url)
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            callback?.onPageFinished(url)
        }

        private fun isRedirected(url: String): Boolean {
            // Implement logic to check if the URL is a redirected URL
            return false
        }
    }

    private val customWebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            webPageTitle = title ?: ""
            webPageDate = System.currentTimeMillis().toString()
            if (isHistory) {
                saveWebsiteInfo(context, false)
                isHistory = false
            }
        }

        override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
            super.onReceivedIcon(view, icon)
            webPageIcon = icon
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            callback?.onProgressChanged(newProgress)
        }
    }

    private var webPageTitle: String = ""
    private var webPageIcon: Bitmap? = null
    private var webPageDate: String? = null

    init {
        webViewClient = customWebViewClient
        webChromeClient = customWebChromeClient
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
    }

    fun setOnWebViewCallbackListener(listener: WebViewCallback) {
        this.callback = listener
    }

    fun handleBackPress(): Boolean {
        return if (canGoBack()) {
            goBack()
            true
        } else {
            false
        }
    }

    private fun getWebsiteInfo(): WebPageInfo {
        return WebPageInfo(
            webPageTitle, url ?: "", webPageDate ?: "",
            selected = false,
            isGone = false
        )
    }

    @SuppressLint("SuspiciousIndentation")
    fun saveWebsiteInfo(context: Context, isMark: Boolean) {
        val beanList: MutableList<WebPageInfo> = DataUtils.loadWebPageInfoList(context, isMark)
            ?: emptyList<WebPageInfo>().toMutableList()
        beanList.add(getWebsiteInfo())
        DataUtils.saveWebPageInfoList(context, beanList, isMark)
    }

    fun getWebsiteInfoList(isMark: Boolean): MutableList<WebPageInfo>? {
        return DataUtils.loadWebPageInfoList(context, isMark)?.asSequence()
            ?.sortedByDescending { it.date }
            ?.toMutableList()
    }

    fun deleteWebsiteInfo(context: Context, isMark: Boolean, date: String) {
        val beanList = DataUtils.loadWebPageInfoList(context, isMark)
        beanList?.removeAll { it.date == date }
        if (beanList != null) {
            DataUtils.saveWebPageInfoList(context, beanList, isMark)
        }
    }

    fun goForwardPage() {
        if (canGoForward()) {
            goForward()
        }
    }

    fun loadCustomWeb(url: String, isHistory: Boolean) {
        this.isHistory = isHistory
        loadUrl = url
        this.clearCache(true)
        this.loadUrl(url)
    }

    fun refreshPage() {
        this.reload()
    }

    fun isAtEndOfForwardHistory(): Boolean {
        return !canGoForward()
    }

    fun isAtStartOfBackHistory(): Boolean {
        return !canGoBack()
    }


    data class WebPageInfo(
        val title: String,
        val url: String,
        val date: String = "",
        var selected: Boolean = false,
        var isGone: Boolean = false
    )
}

