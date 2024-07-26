package com.phoenix.tail.butterfly.eats.concret.upside.bbbee

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.blackData
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.userData
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.NetUtils
import com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.mmmnn.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class AAApp : Application() {

    companion object {
        var isInBackground = false
        var lastBackgroundTime: Long = 0
        private const val DATASTORE_NAME = "app_preferences"
        val Context.SaDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
        lateinit var appComponent: Context
        var isCanHots = true
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = this

        GlobalScope.launch(Dispatchers.IO) {
            if (appComponent.userData == null) {
                appComponent.userData = UUID.randomUUID().toString()
            }
            NetUtils.getServiceData(DataUtils.ccckk_url, {
                appComponent.blackData = it
            }, {})
        }
        hotFun()
    }

    private fun hotFun() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                isInBackground = false
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackgroundTime > 3000 && isCanHots) {
                    (activity as? MainActivity)?.showStartFragment()
                }
            }

            override fun onActivityPaused(activity: Activity) {
                isInBackground = true
                lastBackgroundTime = System.currentTimeMillis()
            }

            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
