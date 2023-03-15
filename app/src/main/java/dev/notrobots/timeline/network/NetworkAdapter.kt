package dev.notrobots.timeline.network

import androidx.annotation.WorkerThread
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class NetworkAdapter(
    /**
     * The value of the User-Agent header sent with every request.
     */
    val userAgent: String,
    /**
     * A customized OkHttpClient instance. Defaults to a default client.
     */
    val http: OkHttpClient
) {
    @WorkerThread
    fun execute(request: Request): Response {
        return http.newCall(request).execute()
    }

    fun request(): Request.Builder {
        return Request.Builder()
            .header("User-Agent", userAgent)
    }
}