package com.phoenix.tail.butterfly.eats.concret.upside.bbbnn

import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager.open

object AdCacheManager {
    private const val EXPIRATION_TIME = 50 * 60 * 1000 // 50 minutes in milliseconds
    private const val EXPIRATION_TIME_OPEN = 4 * 60 * 60 * 1000 // 50 minutes in milliseconds

    private val cache = mutableMapOf<String, MutableList<CachedAd>>()

    fun addAd(type: String, cachedAd: CachedAd) {
        if (cache[type] == null) {
            cache[type] = mutableListOf()
        }
        cache[type]?.add(cachedAd)
    }

    fun getAd(type: String): CachedAd? {
        cleanExpiredAds(type)
        return cache[type]?.firstOrNull()
    }

    fun clearAd(type: String) {
        cache[type]?.firstOrNull()?.also {
            cache[type]?.remove(it)
        }
    }

    private fun cleanExpiredAds(typeData: String) {
        val timeData = if (typeData == open) {
            EXPIRATION_TIME_OPEN
        } else {
            EXPIRATION_TIME
        }
        val currentTime = System.currentTimeMillis()
        for ((type, ads) in cache) {
            cache[type] =
                ads.filter { currentTime - it.timestamp < timeData }.toMutableList()
        }
    }
}

