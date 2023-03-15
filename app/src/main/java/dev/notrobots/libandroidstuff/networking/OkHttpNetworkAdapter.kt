package dev.notrobots.libandroidstuff.networking

import dev.notrobots.timeline.extensions.post
import dev.notrobots.timeline.extensions.url
import okhttp3.*

/**
 * Base network adapter for sending HTTP requests and opening WebSocket connections using
 * Square's [OkHttp](https://square.github.io/okhttp/).
 *
 * This class was taken from [mattbdean/JRAW](https://github.com/mattbdean/JRAW/blob/master/lib/src/main/kotlin/net/dean/jraw/http/OkHttpNetworkAdapter.kt).
 */
//open class OkHttpNetworkAdapter constructor(
//    /**
//     * The value of the User-Agent header sent with every request.
//     */
//    val userAgent: String,
//    /**
//     * A customized OkHttpClient instance. Defaults to a default client.
//     */
//    val http: OkHttpClient
//) {
//    fun execute(r: HttpRequest): Response {
//        return createCall(r).execute()
//    }
//
//    fun connect(url: String, listener: WebSocketListener): WebSocket {
//        val client = OkHttpClient()
//
//        val ws = client.newWebSocket(
//            Request.Builder()
//                .get()
//                .url(url)
//                .build(), listener
//        )
//
//        // Shutdown the ExecutorService so this program can terminate normally
//        client.dispatcher.executorService.shutdown()
//
//        return ws
//    }
//
//    private fun createCall(r: HttpRequest): Call {
//        return http.newCall(compileRequest(r))
//    }
//
//    private fun compileRequest(r: HttpRequest): Request =
//        Request.Builder()
//            .headers(r.headers.newBuilder().set("User-Agent", userAgent).build())
//            .url(r.url)
//            .method(r.method, r.body)
//            .build()
//}
