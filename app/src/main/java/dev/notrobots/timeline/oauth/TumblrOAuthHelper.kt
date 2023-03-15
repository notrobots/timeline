package dev.notrobots.timeline.oauth

import com.github.scribejava.core.model.OAuth2AccessToken
import dev.notrobots.timeline.jumblr.JumblrClient

class TumblrOAuthHelper(
    tokenId: String?,
    tokenStore: TokenStore<OAuth2AccessToken>
) : OAuthHelper(
    tokenId,
    tokenStore,
    TumblrApi20.INSTANCE
) {
    var tumblr: JumblrClient? = null
        private set

    init {
        //tumblr = JumblrClient(accessToken)
    }
}