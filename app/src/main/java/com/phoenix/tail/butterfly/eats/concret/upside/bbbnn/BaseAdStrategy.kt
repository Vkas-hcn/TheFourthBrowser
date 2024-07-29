package com.phoenix.tail.butterfly.eats.concret.upside.bbbnn

import android.util.Log
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager.open
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.CenterUtils.loge
import java.util.Calendar

abstract class BaseAdStrategy : AdStrategy {
    private var loadingAdState: Boolean = false
    private var openLoadOne = false
    protected fun loadNextAd(
        ads: List<AdData>,
        index: Int,
        callback: (Boolean, CachedAd?) -> Unit
    ) {
        if (index >= ads.size) {
            if (ads[0].where == open && !openLoadOne) {
                openLoadOne = true
                loadNextAd(ads, 0, callback)
                return
            }
            callback(false, null)
            return
        }

        val adData = ads[index]
        if (AdCacheManager.getAd(adData.where) != null) {
            Log.e("TAG", "${adData.where} ad have cache ,not load")
            return
        }
        if (loadingAdState) {
            Log.e("TAG", "${adData.where} ad is loading ,not load")
            return
        }
        loadingAdState = true
        Log.e("TAG", "${adData.where} ad load start")
        loadAd(adData) { success, cachedAd ->
            loadingAdState = false
            if (success) {
                callback(true, cachedAd)
            } else {
                loadNextAd(ads, index + 1, callback)
            }
        }
    }

    abstract fun loadAd(adData: AdData, callback: (Boolean, CachedAd?) -> Unit)
}

class OpenAdStrategy : BaseAdStrategy() {
    override fun loadAds(ads: List<AdData>, callback: (Boolean, CachedAd?) -> Unit) {
        loadNextAd(ads.sortedByDescending { it.weights }, 0, callback)
    }

    override fun loadAd(adData: AdData, callback: (Boolean, CachedAd?) -> Unit) {
        Log.e("TAG", "Loading open ad: ${adData.id}-we=${adData.weights}")
        AppOpenAd.load(
            AAApp.appComponent,
            adData.id,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object :
                AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("TAG", "open ad load loadAdError=${loadAdError}: ")
                    callback(false, null)
                }

                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    Log.e("TAG", "open ad load success")
                    callback(true, CachedAd(appOpenAd, System.currentTimeMillis()))
                }
            })
    }
}

class NativeAdStrategy : BaseAdStrategy() {
    override fun loadAds(ads: List<AdData>, callback: (Boolean, CachedAd?) -> Unit) {
        loadNextAd(ads.sortedByDescending { it.weights }, 0, callback)
    }

    override fun loadAd(adData: AdData, callback: (Boolean, CachedAd?) -> Unit) {
        Log.e("TAG", "Loading backHome ad: ${adData.id}")
        val adLoader = AdLoader.Builder(AAApp.appComponent, adData.id)
            .forNativeAd { naAd ->
                Log.e("TAG", "backHome ad load success")
                callback(true, CachedAd(naAd, System.currentTimeMillis()))


            }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("TAG", "backHome Ad loading failed:${loadAdError}")
                    callback(false, null)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.e("TAG", "Click native ad")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}

class IntAdStrategy : BaseAdStrategy() {
    override fun loadAds(ads: List<AdData>, callback: (Boolean, CachedAd?) -> Unit) {
        loadNextAd(ads.sortedByDescending { it.weights }, 0, callback)
    }

    override fun loadAd(adData: AdData, callback: (Boolean, CachedAd?) -> Unit) {
        Log.e("TAG", "Loading ${adData.where} ad: ${adData.id}-we=${adData.weights}")
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            AAApp.appComponent,
            adData.id,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(loadedAd: InterstitialAd) {
                    Log.e("TAG", "${adData.where} ad load success")
                    callback(true, CachedAd(loadedAd, System.currentTimeMillis()))

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("TAG", "${adData.where} Ad loading failed:${loadAdError}")
                    callback(false, null)
                }
            })
    }
}

