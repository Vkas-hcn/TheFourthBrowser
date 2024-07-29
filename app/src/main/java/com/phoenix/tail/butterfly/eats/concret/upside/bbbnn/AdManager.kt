package com.phoenix.tail.butterfly.eats.concret.upside.bbbnn

import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.clickInt
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.lastResetTime
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.showInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AdManager {
    const val open = "open"
    const val backHome = "backHome"
    const val clickInt = "clickInt"
    private val strategies = mutableMapOf<String, AdStrategy>()

    init {
        strategies["open"] = OpenAdStrategy()
        strategies["backHome"] = IntAdStrategy()
        strategies["clickInt"] = IntAdStrategy()
        resetCountsIfNeeded()
    }

    private fun resetCountsIfNeeded() {
        val currentTime = System.currentTimeMillis()
        val lastResetTime = AAApp.appComponent.lastResetTime?.toLong() ?: System.currentTimeMillis()
        val nextResetTime = lastResetTime + 24 * 60 * 60 * 1000
        if (currentTime >= nextResetTime) {
            AAApp.appComponent.showInt = "0"
            AAApp.appComponent.clickInt = "0"
            AAApp.appComponent.lastResetTime = currentTime.toString()
        }
    }

    private fun moreAdNumLoad(ads: AdDataList, loadFun: () -> Unit) {
        if (((AAApp.appComponent.showInt?.toInt()
                ?: 0) < ads.showUpperLimit) && ((AAApp.appComponent.clickInt?.toInt()
                ?: 0) < ads.clickUpperLimit)
        ) {
            loadFun()
        } else {
            Log.e("TAG", "Ad loading limit reached. Cannot load more ads today.")
        }
    }

    fun loadAd(type: String) {
        val ads = DataUtils.getAdsData()
        val adData = when (type) {
            "open" -> {
                ads.open
            }

            "backHome" -> {
                ads.backHome
            }

            "clickInt" -> {
                ads.clickInt
            }

            else -> {
                ads.open
            }
        }
        resetCountsIfNeeded()
        moreAdNumLoad(ads) {
            strategies[type]?.loadAds(adData) { success, cachedAd ->
                if (success && cachedAd != null) {
                    AdCacheManager.addAd(type, cachedAd)
                }
            }
        }
    }

    fun onAdClickedFun() {
        var clickNum = AAApp.appComponent.clickInt?.toInt()
            ?: 0
        clickNum++
        AAApp.appComponent.clickInt = clickNum.toString()
    }

    fun onAdShowedFun() {
        var showNum = AAApp.appComponent.showInt?.toInt()
            ?: 0
        showNum++
        AAApp.appComponent.showInt = showNum.toString()
    }

    fun canShowAd(type: String): Boolean {
        val cachedAd = AdCacheManager.getAd(type)
        return cachedAd != null
    }

    fun jumFunAd(type: String): Boolean {
        val ads = DataUtils.getAdsData()
        val jumState: Boolean = !(((AAApp.appComponent.showInt?.toInt()
            ?: 0) < ads.showUpperLimit) && ((AAApp.appComponent.clickInt?.toInt()
            ?: 0) < ads.clickUpperLimit))
        return !canShowAd(type) && jumState
    }

    fun showAd(type: String, activity: FragmentActivity, onAdDismissed: () -> Unit) {
        val cachedAd = AdCacheManager.getAd(type)
        if (cachedAd != null) {
            when (type) {
                open -> {
                    showOpenAda(activity, cachedAd, onAdDismissed)
                }

                clickInt, backHome -> {
                    showClickAda(activity, cachedAd, type, onAdDismissed)
                }
            }
        }
    }

    private fun showOpenAda(
        activity: FragmentActivity,
        cachedAd: CachedAd?,
        onAdDismissed: () -> Unit
    ) {
        val callback: FullScreenContentCallback by lazy {
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    val baseAct = activity as AppCompatActivity
                    baseAct.lifecycleScope.launch {
                        delay(200L)
                        if (Lifecycle.State.RESUMED == baseAct.lifecycle.currentState) {
                            onAdDismissed.invoke()
                        }
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    onAdShowedFun()
                    AdCacheManager.clearAd(open)
                }

                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    AdCacheManager.clearAd(open)
                }

                override fun onAdClicked() {
                    Log.e("TAG", "onAdClicked: open_start")
                    onAdClickedFun()
                }
            }
        }

        fun showAdMobFullScreenAd() {
            when (val adF = cachedAd?.adObject) {
                is AppOpenAd -> {
                    adF.run {
                        fullScreenContentCallback = callback
                        show(activity)
                        Log.e("TAG", "showAd open ")
                    }
                }

                else -> onAdDismissed.invoke()
            }
        }
        showAdMobFullScreenAd()
    }

    private fun showClickAda(
        activity: FragmentActivity,
        cachedAd: CachedAd?,
        type: String,
        onAdDismissed: () -> Unit
    ) {
        (cachedAd?.adObject as InterstitialAd).fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    activity.lifecycleScope.launch {
                        delay(200L)
                        if (Lifecycle.State.RESUMED == activity.lifecycle.currentState) {
                            onAdDismissed.invoke()
                        }
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    onAdShowedFun()
                    AdCacheManager.clearAd(type)
                    loadAd(type)
                }

                override fun onAdClicked() {
                    onAdClickedFun()
                }
            }
        if (activity.lifecycle.currentState == Lifecycle.State.RESUMED) {
            Log.e("TAG", "$type ad is show")
            (cachedAd.adObject as InterstitialAd).show(activity)
        }
    }
}

