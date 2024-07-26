package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.ssstt

import androidx.lifecycle.MutableLiveData
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.phoenix.tail.butterfly.eats.concret.upside.BuildConfig
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseViewModel

class StartViewModel: BaseViewModel() {
    val liveHomeData = MutableLiveData<Boolean>()
    var isFinish = false
}