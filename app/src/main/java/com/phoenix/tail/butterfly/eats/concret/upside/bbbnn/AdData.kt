package com.phoenix.tail.butterfly.eats.concret.upside.bbbnn

data class AdData(
    val ssssffzz: String,
    val ppppffmm: String,
    val ttttpppee: String,
    val wwwwtttss: Int,
    val wwwweeeee: String
)
data class AdDataList(
    val showUpperLimit: Int,
    val clickUpperLimit: Int,
    val oooonn: MutableList<AdData>,
    val bbbbhhee: MutableList<AdData>,
    val cccckkii: MutableList<AdData>,
)
data class CachedAd(
    val adObject: Any,
    val timestamp: Long
)
