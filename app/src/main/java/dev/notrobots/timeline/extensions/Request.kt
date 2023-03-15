package dev.notrobots.timeline.extensions

import net.dean.jraw.addQueryParameters
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request

fun Request.Builder.method(method: String, body: Map<String, String>) = apply {
    val formBodyBuilder = FormBody.Builder()

    for ((k, v) in body) {
        formBodyBuilder.add(k, v)
    }

    method(method, formBodyBuilder.build())
}

fun Request.Builder.method(method: String, vararg body: Pair<String, String>) = method(method, body.toMap())

fun Request.Builder.post(body: Map<String, String>) = method("POST", body)

fun Request.Builder.post(vararg body: Pair<String, String>) = method("POST", body.toMap())

fun Request.Builder.patch(body: Map<String, String>) = method("PATCH", body)

fun Request.Builder.patch(vararg body: Pair<String, String>) = method("PATCH", body.toMap())

fun Request.Builder.put(body: Map<String, String>) = method("PUT", body)

fun Request.Builder.put(vararg body: Pair<String, String>) = method("PUT", body.toMap())

fun Request.Builder.delete(body: Map<String, String>) = method("DELETE", body)

fun Request.Builder.delete(vararg body: Pair<String, String>) = method("DELETE", body.toMap())

fun Request.Builder.url(
    url: String,
    query: Map<String, String>
) = apply {
    val url = url.toHttpUrlOrNull() ?: throw Exception("Malformed url: $url")
    val urlBuilder = url.newBuilder()

    if (query.isNotEmpty()) {
        urlBuilder.addQueryParameters(query)
    }

    url(urlBuilder.build())
}

fun Request.Builder.url(
    host: String,
    path: String,
    secure: Boolean = true,
    query: Map<String, String> = emptyMap()
) = apply {
    val urlString = buildString {
        append(if (secure) "https" else "http")

        if (host.startsWith("https")) {
            append(host.removePrefix("https"))
        } else if (host.startsWith("http")) {
            append(host.removePrefix("http"))
        }

        if (path.startsWith('/')) {
            append(path.removePrefix("/"))
        }
    }

    url(urlString, query)
}