package dev.notrobots.timeline.models

data class SocialFilter(
    var fakeTwitter: Boolean,
    var fakeReddit: Boolean,
    val enabledProfiles: Map<String, List<Profile>>
)