package dev.notrobots.timeline.oauth

import com.squareup.moshi.*
import java.util.*
import java.util.concurrent.TimeUnit

//class OAuthToken(
//    @Json(name = "access_token")
//    val accessToken: String,
//    @Json(name = "refresh_token")
//    var refreshToken: String,
//    @Json(name = "expires_in")
//    val expiresIn: Int,
//    @Json(name = "token_type")
//    val tokenType: String,
//    @Json(name = "scope")
//    val scope: String
//) {
//    val expireDate = Date(Date().time + TimeUnit.SECONDS.toMillis(expiresIn.toLong()))
//
//    fun isExpired(now: Long): Boolean {
//        return now > expireDate.time
//    }
//}