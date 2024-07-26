package com.phoenix.tail.butterfly.eats.concret.upside.uuuuss

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp.Companion.SaDataStore
import com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.wwwtt.CustomWebView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object DataUtils {
    val pp_url = "https://www.google.com/"
    const val ccckk_url = "https://test.onlinenetwork.link/tKyBVzPf/kmFHD/RMA/"

    const val ins_url = "https://www.baidu.com/"
    const val fb_url = "https://www.baidu.com/"
    const val netfilx_url = "https://www.baidu.com/"
    const val ytb_url = "https://www.baidu.com/"


    const val ser_google = "https://www.google.com/search?q="
    const val ser_bing = "https://www.bing.com/search?q="
    const val ser_yahoo = "https://search.yahoo.com/search?p="
    const val ser_duck = "https://duckduckgo.com/?t=h_&q="

    private const val FILENAME_MARKS = "web_page_marks.json"
    private const val FILENAME_HISTORY = "web_page_history.json"

    private fun stringPreferenceKey(key: String) = stringPreferencesKey(key)

    var Context.userData: String?
        get() = runBlocking {
            SaDataStore.data
                .map { preferences -> preferences[stringPreferenceKey("user_data")] }
                .first()
        }
        set(value) {
            runBlocking {
                SaDataStore.edit { preferences ->
                    preferences[stringPreferenceKey("user_data")] = value.orEmpty()
                }
            }
        }

    //black
    var Context.blackData: String?
        get() = runBlocking {
            SaDataStore.data
                .map { preferences -> preferences[stringPreferenceKey("blackData")] }
                .first()
        }
        set(value) {
            runBlocking {
                SaDataStore.edit { preferences ->
                    preferences[stringPreferenceKey("blackData")] = value.orEmpty()
                }
            }
        }

    var Context.searchData: String?
        get() = runBlocking {
            SaDataStore.data
                .map { preferences -> preferences[stringPreferenceKey("searchData")] }
                .first()
        }
        set(value) {
            runBlocking {
                SaDataStore.edit { preferences ->
                    preferences[stringPreferenceKey("searchData")] = value.orEmpty()
                }
            }
        }


    // 保存数据到文件
    fun saveWebPageInfoList(
        context: Context,
        webPageInfoList: MutableList<CustomWebView.WebPageInfo>,
        isMark: Boolean
    ) {
        val jsonName = if (isMark) {
            FILENAME_MARKS
        } else {
            FILENAME_HISTORY
        }
        val file = File(context.filesDir, jsonName)
        val json = Gson().toJson(webPageInfoList)

        FileOutputStream(file).use { fos ->
            OutputStreamWriter(fos).use { osw ->
                osw.write(json)
            }
        }
    }

    // 从文件中加载数据
    fun loadWebPageInfoList(
        context: Context,
        isMark: Boolean
    ): MutableList<CustomWebView.WebPageInfo>? {
        val jsonName = if (isMark) {
            FILENAME_MARKS
        } else {
            FILENAME_HISTORY
        }
        val file = File(context.filesDir, jsonName)
        if (!file.exists()) return null

        val json = FileInputStream(file).use { fis ->
            InputStreamReader(fis).use { isr ->
                isr.readText()
            }
        }

        val typeToken = object : TypeToken<MutableList<CustomWebView.WebPageInfo>>() {}.type
        return Gson().fromJson(json, typeToken)
    }
}
