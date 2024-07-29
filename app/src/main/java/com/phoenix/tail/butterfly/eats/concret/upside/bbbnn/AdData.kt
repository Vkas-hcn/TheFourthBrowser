package com.phoenix.tail.butterfly.eats.concret.upside.bbbnn

data class AdData(
    val id: String,
    val platform: String,
    val type: String,
    val weights: Int,
    val where: String
)
data class AdDataList(
    val showUpperLimit: Int,
    val clickUpperLimit: Int,
    val open: MutableList<AdData>,
    val backHome: MutableList<AdData>,
    val clickInt: MutableList<AdData>,
)
data class CachedAd(
    val adObject: Any,
    val timestamp: Long
)
