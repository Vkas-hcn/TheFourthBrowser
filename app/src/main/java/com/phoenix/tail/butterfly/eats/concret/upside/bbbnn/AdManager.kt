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
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.cccckkii
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.lastResetTime
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.showInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AdManager {
    const val oooonn = "oooonn"
    const val bbbbhhee = "bbbbhhee"
    const val cccckkii = "cccckkii"
    private val strategies = mutableMapOf<String, AdStrategy>()

    init {
        strategies["oooonn"] = OpenAdStrategy()
        strategies["bbbbhhee"] = IntAdStrategy()
        strategies["cccckkii"] = IntAdStrategy()
        resetCountsIfNeeded()
    }

    private fun resetCountsIfNeeded() {
        val currentTime = System.currentTimeMillis()
        val lastResetTime = AAApp.appComponent.lastResetTime?.toLong() ?: System.currentTimeMillis()
        val nextResetTime = lastResetTime + 24 * 60 * 60 * 1000
        if (currentTime >= nextResetTime) {
            AAApp.appComponent.showInt = "0"
            AAApp.appComponent.cccckkii = "0"
            AAApp.appComponent.lastResetTime = currentTime.toString()
        }
    }

    private fun moreAdNumLoad(ads: AdDataList, loadFun: () -> Unit) {
        if (((AAApp.appComponent.showInt?.toInt()
                ?: 0) < ads.showUpperLimit) && ((AAApp.appComponent.cccckkii?.toInt()
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
            "oooonn" -> {
                ads.oooonn
            }

            "bbbbhhee" -> {
                ads.bbbbhhee
            }

            "cccckkii" -> {
                ads.cccckkii
            }

            else -> {
                ads.oooonn
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
        var clickNum = AAApp.appComponent.cccckkii?.toInt()
            ?: 0
        clickNum++
        AAApp.appComponent.cccckkii = clickNum.toString()
    }

    fun onAdShowedFun() {
        var showNum = AAApp.appComponent.showInt?.toInt()
            ?: 0
        showNum++
        AAApp.appComponent.showInt = showNum.toString()
    }

    fun canShowAd(type: String): Boolean {
        val cachedAd = AdCacheManager.getAd(type)
        if (cachedAd == null) {
            loadAd(type)
        }
        return cachedAd != null
    }

    fun jumFunAd(type: String): Boolean {
        val ads = DataUtils.getAdsData()
        val jumState: Boolean = !(((AAApp.appComponent.showInt?.toInt()
            ?: 0) < ads.showUpperLimit) && ((AAApp.appComponent.cccckkii?.toInt()
            ?: 0) < ads.clickUpperLimit))
        return (!canShowAd(type) && jumState)||(AAApp().getIsBlack()&& type!= oooonn)
    }

    fun showAd(type: String, activity: FragmentActivity, onAdDismissed: () -> Unit) {
        val cachedAd = AdCacheManager.getAd(type)
        if (cachedAd != null) {
            when (type) {
                oooonn -> {
                    showOpenAda(activity, cachedAd, onAdDismissed)
                }

                cccckkii, bbbbhhee -> {
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
                    AdCacheManager.clearAd(oooonn)
                }

                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    AdCacheManager.clearAd(oooonn)
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
                        Log.e("TAG", "showAd oooonn ")
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

