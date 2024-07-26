package com.phoenix.tail.butterfly.eats.concret.upside.uuuuss

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.NetworkInterface
import java.net.URL

object NetUtils {
    interface Callback {
        fun onSuccess(result: String)
        fun onFailure(error: String)
    }

    fun getServiceData(url: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        Thread {
            try {
                val urlObj = URL(url)
                val conn = urlObj.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                val customHeaders = mapOf(
                    "PXNVV" to "ZZ",
                    "QAVD" to "skt.vs.wbg.who.is.champion.flashvpn",
                )
                for ((key, value) in customHeaders) {
                    conn.setRequestProperty(key, value)
                }
                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = conn.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = reader.readText()
                    reader.close()
                    onSuccess(response)
                } else {
                    onError("Error from server: $responseCode")
                }
            } catch (e: Exception) {
                onError("Network error or other exception: ${e.message}")
            }
        }.start()
    }


    fun post(url: String, body: Any, callback: Callback) {
        Thread {
            var connection: HttpURLConnection? = null
            try {
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                connection = urlConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true
                connection.doInput = true

                // Write body to the request
                BufferedWriter(OutputStreamWriter(connection.outputStream, "UTF-8")).use { writer ->
                    writer.write(body.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                    callback.onSuccess(responseBody)
                } else {
                    val errorBody = connection.errorStream.bufferedReader().use { it.readText() }
                    callback.onFailure(errorBody)
                }
            } catch (e: IOException) {
                callback.onFailure("Network error: ${e.message}")
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    fun isNetworkConnected(): Boolean {
        try {
            val interfaces: List<NetworkInterface> =
                NetworkInterface.getNetworkInterfaces().asSequence()
                    .toList()
                    .filter { it.isUp }

            for (networkInterface in interfaces) {
                val hardwareAddress = networkInterface.hardwareAddress
                if (hardwareAddress != null && !isLoopbackAddress(hardwareAddress)) {
                    return true
                }
            }
        } catch (e: java.net.SocketException) {
            e.printStackTrace()
        }

        return false
    }

    private fun isLoopbackAddress(address: ByteArray): Boolean {
        return address[0] == 127.toByte()
    }
}