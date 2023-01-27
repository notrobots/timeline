package dev.notrobots.timeline.util

import dev.notrobots.androidstuff.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

object Network {
    fun downloadImageSync(url: String): ByteArray {
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            val bytes = conn.inputStream.readBytes()

            conn.disconnect()

            return bytes
        } catch (e: Exception) {
            Logger.loge("Network error", e)
            return byteArrayOf()
        }
    }

    fun downloadImage(
        url: String,
        onSuccess: (ByteArray) -> Unit
    ) {
        val io = CoroutineScope(Dispatchers.IO)

        io.launch {
            val bytes = downloadImageSync(url)

            onSuccess(bytes)
        }
    }
}