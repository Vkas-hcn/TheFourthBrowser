package com.phoenix.tail.butterfly.eats.concret.upside.bbbee

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.ads.AdActivity
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.blackData
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.userData
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.NetUtils
import com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.mmmnn.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class AAApp : Application() {
    var adActivity: Activity? = null

    companion object {
        var isInBackground = false
        var lastBackgroundTime: Long = 0
        private const val DATASTORE_NAME = "app_preferences"
        val Context.SaDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
        lateinit var appComponent: Context
        var isCanHots = false
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = this

        GlobalScope.launch(Dispatchers.IO) {
            if (appComponent.userData == null) {
                appComponent.userData = UUID.randomUUID().toString()
            }
            if (appComponent.blackData == null || appComponent.blackData!!.isBlank()) {
                NetUtils.getBlackData(DataUtils.ccckk_url, {
                    Log.e("TAG", "getBlackData: $it")
                    this@AAApp.blackData = it
                }, {

                })
            }
        }
        hotFun()
    }

    private fun hotFun() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {
                if (activity is AdActivity) {
                    adActivity = activity
                }
            }

            override fun onActivityResumed(activity: Activity) {
                isInBackground = false
                val currentTime = System.currentTimeMillis()
                if (isCanHots) {
                    Log.e("TAG", "onActivityResumed: ")

                    (activity as? MainActivity)?.showStartFragment()
                    isCanHots = false
                }
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
                Log.e("TAG", "onActivityStopped: ")
                isInBackground = true
                isCanHots = false
                GlobalScope.launch {
                    delay(3000)
                    if (isInBackground) {
                        adActivity?.finish()
                        isCanHots = true
                    }
                }
                lastBackgroundTime = System.currentTimeMillis()
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
