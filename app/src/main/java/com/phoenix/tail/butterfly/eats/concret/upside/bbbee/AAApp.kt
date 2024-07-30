package com.phoenix.tail.butterfly.eats.concret.upside.bbbee

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdActivity
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.aaajjs
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
        var jumpMark = -1
        var markWeburl :String=""
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
        setAAAppjjjst(this)
        hotFun()
    }

    fun getIsBlack(): Boolean {
        return this@AAApp.blackData != "ph"
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
                if (isCanHots) {
                    Log.e("TAG", "onActivityResumed: ")
                    (activity as? MainActivity)?.showStartFragment()
                    isCanHots = false
                }
                Adjust.onResume()
            }

            override fun onActivityPaused(activity: Activity) {
                Adjust.onPause()
            }

            override fun onActivityStopped(activity: Activity) {
                Log.e("TAG", "onActivityStopped: ${activity is AdActivity}")
                if (activity is AdActivity) {
                    return
                }
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

    @SuppressLint("HardwareIds")
    private fun setAAAppjjjst(application: Application) {
        Adjust.addSessionCallbackParameter(
            "customer_user_id",
            Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
        )
        val appToken = "ih2pm2dr3k74"
        val environment: String = AdjustConfig.ENVIRONMENT_SANDBOX
        val config = AdjustConfig(application, appToken, environment)
        config.needsCost = true
        config.setOnAttributionChangedListener { attribution ->
            Log.e("TAG", "adjust=${attribution}")
            if (appComponent.aaajjs != "finish" && attribution.network.isNotEmpty() && attribution.network.contains(
                    "organic",
                    true
                ).not()
            ) {
                appComponent.aaajjs = "finish"
            }
        }
        Adjust.onCreate(config)
    }
}
