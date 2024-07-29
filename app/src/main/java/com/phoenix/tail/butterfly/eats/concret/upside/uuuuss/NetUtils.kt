package com.phoenix.tail.butterfly.eats.concret.upside.uuuuss

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.NetworkInterface
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread
object NetUtils {
    fun getBlackData(url: String, onSuccess:(response:String)->Unit,onErrorAction: (error:String)->Unit) {
        val urlBuilder = StringBuilder(url)
        val map = CenterUtils.blackData()
        if (map.isNotEmpty()) {
            urlBuilder.append("?")
            map.forEach { entry ->
                urlBuilder.append(
                    "${URLEncoder.encode(entry.key, StandardCharsets.UTF_8.toString())}=" +
                            "${URLEncoder.encode(entry.value.toString(), StandardCharsets.UTF_8.toString())}&"
                )
            }
            urlBuilder.setLength(urlBuilder.length - 1)  // Remove the last '&'
        }

        thread {
            var connection: HttpURLConnection? = null
            try {
                val requestUrl = URL(urlBuilder.toString())
                connection = requestUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.use { it.readText() }
                    onSuccess(response)
                } else {
                    onErrorAction("Error: $responseCode")
                }
            } catch (e: Exception) {
                onErrorAction(e.toString())
            } finally {
                connection?.disconnect()
            }
        }
    }

//
//
//    fun post(url: String, body: Any, callback: Callback) {
//        Thread {
//            var connection: HttpURLConnection? = null
//            try {
//                val urlConnection = URL(url).openConnection() as HttpURLConnection
//                connection = urlConnection
//                connection.requestMethod = "POST"
//                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
//                connection.setRequestProperty("Accept", "application/json")
//                connection.doOutput = true
//                connection.doInput = true
//
//                // Write body to the request
//                BufferedWriter(OutputStreamWriter(connection.outputStream, "UTF-8")).use { writer ->
//                    writer.write(body.toString())
//                    writer.flush()
//                }
//
//                val responseCode = connection.responseCode
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
//                    callback.onSuccess(responseBody)
//                } else {
//                    val errorBody = connection.errorStream.bufferedReader().use { it.readText() }
//                    callback.onFailure(errorBody)
//                }
//            } catch (e: IOException) {
//                callback.onFailure("Network error: ${e.message}")
//            } finally {
//                connection?.disconnect()
//            }
//        }.start()
//    }
//
//    fun isNetworkConnected(): Boolean {
//        try {
//            val interfaces: List<NetworkInterface> =
//                NetworkInterface.getNetworkInterfaces().asSequence()
//                    .toList()
//                    .filter { it.isUp }
//
//            for (networkInterface in interfaces) {
//                val hardwareAddress = networkInterface.hardwareAddress
//                if (hardwareAddress != null && !isLoopbackAddress(hardwareAddress)) {
//                    return true
//                }
//            }
//        } catch (e: java.net.SocketException) {
//            e.printStackTrace()
//        }
//
//        return false
//    }

    private fun isLoopbackAddress(address: ByteArray): Boolean {
        return address[0] == 127.toByte()
    }
}