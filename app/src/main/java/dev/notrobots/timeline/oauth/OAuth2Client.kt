package dev.notrobots.timeline.oauth

import com.github.scribejava.core.model.OAuth2AccessToken
import com.squareup.moshi.JsonAdapter
import dev.notrobots.androidstuff.extensions.toStringOrEmpty
import dev.notrobots.timeline.extensions.url
import dev.notrobots.timeline.network.NetworkAdapter
import net.dean.jraw.http.NetworkException
import net.dean.jraw.oauth.OAuthException
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

/**
 * OAuth2 authentication utility for helping you acquire access and refresh tokens from a service.
 */
//open class OAuth2Client @JvmOverloads constructor(
//    var tokenId: String?,
//    protected val tokenStore: TokenStore<*>,
//    protected val clientId: String,
//    protected val clientSecret: String,
//    protected val userAgent: String,
//    protected val state: String,
//    protected val authorizationRequestUrl: String,
//    protected val tokenRequestUrl: String,
//    protected val scope: List<String>,
//    protected val finalRedirectUri: String,
//    protected val redirectUri: String? = null,
//) {
//    val baseRequest = {
//        http.request()
//
//
////            .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
//    }
//    val http = NetworkAdapter(userAgent, httpClient)
//
//    @Inject
//    protected lateinit var httpClient: OkHttpClient
//
//    @Inject
//    protected lateinit var oAuthTokenAdapter: JsonAdapter<OAuthToken>
//
//    init {
//        tokenId?.let {
//            tokenStore.fetch(it)?.let {
//                onInit(it)
//            }
//        }
//    }
//
//    protected open fun onCreateAuthorizationRequestQuery(): Map<String, String> {
//        val query = mutableMapOf(
//            "client_id" to clientId,
//            "response_type" to "code",
//            "state" to state,
//            "scope" to scope.joinToString(" ")
//        )
//
//        redirectUri?.let {
//            query.put("redirect_uri", it)
//        }
//
//        return query
//    }
//
//    protected open fun onCreateAuthorizationRequestUrl(request: Request.Builder) {
//
//    }
//
//    protected open fun onCreateTokenRequestQuery(code: String): Map<String, String> {
//        val query = mutableMapOf(
//            "grant_type" to "authorization_code",
//            "code" to code,
//            "client_id" to clientId,
//            "client_secret" to clientSecret
//        )
//
//        redirectUri?.let {
//            query.put("redirect_uri", it)
//        }
//
//        return query
//    }
//
//    protected open fun onCreateTokenRequestUrl(request: Request.Builder) {
//
//    }
//
//    protected open fun onAccessTokenGranted(token: OAuth2AccessToken) {
//        //TODO: Create client with the token
//    }
//
//    protected open fun onInit(accessToken: OAuthToken) {
//
//    }
//
//    fun isFinalRequestUrl(url: String): Boolean {
//        val httpUrl = url.toHttpUrlOrNull() ?: throw IllegalArgumentException("Malformed URL: $url")
//
//        return httpUrl.toString().startsWith(finalRedirectUri) &&
//               httpUrl.queryParameter("state") != null
//
//    }
//
//    fun getAuthorizationUrl(): String { //xxx Can be a final value
//        return baseRequest()
//            .url(authorizationRequestUrl, onCreateAuthorizationRequestQuery())
//            .apply(::onCreateAuthorizationRequestUrl)
//            .build().url.toString()
//    }
//
//    fun requestAccessToken(url: String): OAuthToken {
//        val httpUrl = url.toHttpUrlOrNull()
//        val query = httpUrl?.queryParameterNames ?: listOf()
//
//        requireNotNull(httpUrl) {
//            "Url malformed: $url"
//        }
//
//        if ("error" in query)
//            throw OAuthException("Reddit responded with error: ${httpUrl.queryParameter("error")}")
//        if ("state" !in query)
//            throw IllegalArgumentException("Final redirect URL did not contain the 'state' query parameter")
//        if (httpUrl.queryParameter("state") != state)
//            throw IllegalStateException("State did not match")
//        if ("code" !in query)
//            throw IllegalArgumentException("Final redirect URL did not contain the 'code' query parameter")
//
//        val code = httpUrl.queryParameter("code")!!
//
//        try {
//            val request = baseRequest()
//                .url(tokenRequestUrl, onCreateTokenRequestQuery(code))
//                .apply(::onCreateTokenRequestUrl)
//                .build()
//            val response = http.execute(request)
//
//            // TODO Create the social client with the token
//
//            val token = oAuthTokenAdapter.fromJson(response.body.toStringOrEmpty())
//
//            requireNotNull(token) {
//                "OAuth token malformed"
//            }
//
//            onAccessTokenGranted(token)
//
//            return token
//        } catch (e: NetworkException) {
//            if (e.res.code == 401)
//                throw Exception("Unauthorized. Invalid client ID/secret", e)
//
//            throw e
//        }
//    }
//
//    fun requestRefreshToken() {
//
//    }
//}