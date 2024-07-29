package com.phoenix.tail.butterfly.eats.concret.upside.bbbnn

interface AdStrategy {
    fun loadAds(ads: List<AdData>, callback: (Boolean, CachedAd?) -> Unit)
}